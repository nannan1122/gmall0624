package com.atguigu.gmall0624.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0624.bean.CartInfo;
import com.atguigu.gmall0624.bean.SkuInfo;
import com.atguigu.gmall0624.cart.constant.CartConst;
import com.atguigu.gmall0624.cart.mapper.CartInfoMapper;
import com.atguigu.gmall0624.config.RedisUtil;
import com.atguigu.gmall0624.service.CartService;
import com.atguigu.gmall0624.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
   private RedisUtil redisUtil;
    @Autowired
   private CartInfoMapper cartInfoMapper;

    @Reference
    private ManageService manageService;



    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        Jedis jedis = redisUtil.getJedis();
        String cartKey= CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        // 添加之前有没有判断缓存中是否有购物车的key！
       if (!jedis.exists(cartKey)){
           // 加载数据库的数据到缓存！
           loadCartCache(userId);
       }

        //查数据库有无该商品
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("skuId",skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOneByExample(example);
        if (cartInfoExist!=null){
            // 数量更新
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            // 初始化实时价格
            cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());
            // 更新数据库
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
            // 更新redis！ cartInfoExist
           // jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfoExist));

        }else {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1 = new CartInfo();
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuNum(skuNum);
            cartInfo1.setSkuId(skuId);
            cartInfo1.setUserId(userId);
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setSkuName(skuInfo.getSkuName());

            // 添加到数据库！
            cartInfoMapper.insertSelective(cartInfo1);
            // 更新redis！ cartInfo1
            // jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfo1));
            cartInfoExist = cartInfo1;
        }
        // 最后都要添加到缓存
        String cartInfoJson = JSON.toJSONString(cartInfoExist);
            jedis.hset(cartKey,skuId, cartInfoJson);
            //设置过期时间
            setCartkeyExpireTime(userId, jedis, cartKey);
            // 关闭redis！
            jedis.close();




    }


    private void setCartkeyExpireTime(String userId, Jedis jedis, String cartKey) {
        // 根据user得过期时间设置
        // 获取用户的过期时间 user:userId:info
        String userKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USERINFOKEY_SUFFIX;

        // 用户key 存在，登录。
        Long expireTime = null;
        if (jedis.exists(userKey)){
            expireTime = jedis.ttl(userKey);
            jedis.expire(cartKey,expireTime.intValue());
        }else{
            // 给购物车的key 设置
            jedis.expire(cartKey,7*24*3600);
        }

    }

//购物车列表
    @Override
    public List<CartInfo> getCartList(String userId) {
         /*
    1.  先走缓存获取redis中的购物车数据
    2.  如果redis 没有，从mysql 获取并放入缓存
     */
        List<CartInfo> cartInfoList = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        List<String> stringList = jedis.hvals(cartKey);
        if (stringList!=null && stringList.size()>0){ //缓存有取出转化成对象并放入list
            for (String cartJson : stringList) {
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            //排序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        }else {//缓存无--查数据库，放入缓存
            cartInfoList = loadCartCache(userId);
            return cartInfoList;
        }

    }



    public List<CartInfo> loadCartCache(String userId) {
        //查数据库，放入缓存
        // 使用实时价格：将skuInfo.price 价格赋值 cartInfo.skuPrice
        // 所以需要多表查询
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);

        //数据库也没，返回空
        if (cartInfoList==null || cartInfoList.size()==0){
            return  null;
        }

        //放入缓存
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 定义key：user:userId:cart
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;

        HashMap<String, String> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(),JSON.toJSONString(cartInfo));
        }
        jedis.hmset(cartKey,map);

        //设置超时时间
        setCartkeyExpireTime(userId, jedis, cartKey);
        jedis.close();
        return cartInfoList;

    }

//合并
    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartTempList, String userId) {
        // 获取到登录时购物车数据
        List<CartInfo> cartInfoListLogin = cartInfoMapper.selectCartListWithCurPrice(userId);

       //登录购物车有数据
        if (cartInfoListLogin!=null && cartInfoListLogin.size()>0){
                for (CartInfo cartInfoNoLogin : cartTempList) {
                    boolean isMatch = false;
                    // 如果说数据库中一条数据都没有？
                    for (CartInfo cartInfoLogin : cartInfoListLogin) {
                        // 操作 37 38 可能会发生异常？ |
                        if (cartInfoNoLogin.getSkuId().equals(cartInfoLogin.getSkuId())){
                            // 如果都有某商品，则合并到登录的，数量相加
                            cartInfoLogin.setSkuNum(cartInfoLogin.getSkuNum()+cartInfoNoLogin.getSkuNum());

                            // 更新数据库
                            cartInfoMapper.updateByPrimaryKeySelective(cartInfoLogin);
                            isMatch=true;
                        }
                    }
                    // 表示登录的购物车数据与未登录购物车数据没用匹配上！ 39    1
                    if (!isMatch){
                        //  直接添加到数据库
                        cartInfoNoLogin.setId(null);
                        cartInfoNoLogin.setUserId(userId);
                        cartInfoMapper.insertSelective(cartInfoNoLogin);
                    }
                }

            //登录购物车无数据
        }else {
                    // 因为直接查询得数据库，意味着数据库为空！直接添加到数据库！
                    for (CartInfo cartInfo : cartTempList) {
                        cartInfo.setId(null);
                        cartInfo.setUserId(userId);
                        cartInfoMapper.insertSelective(cartInfo);
                    }
        }

        // 汇总数据 37 38 39
        List<CartInfo> cartInfoList = loadCartCache(userId);//方法中查数据库放缓存

        //合并之后的数据与未登录购物车数据进行合并

        for (CartInfo cartInfoDB : cartInfoList) {
            for (CartInfo cartInfo : cartTempList) {
                // skuId 相同
                if (cartInfoDB.getSkuId().equals(cartInfo.getSkuId())){
                    // 合并未登录选中的数据
                    // 如果数据库中为1，未登录中也为1 不用修改！
                    if ("1".equals(cartInfo.getIsChecked())){
                        if (!"1".equals(cartInfoDB.getIsChecked())){
                            // 修改数据库字段为1
                            cartInfoDB.setIsChecked("1");
                            // 修改商品状态为被选中
                            checkCart(cartInfo.getIsChecked(),cartInfo.getSkuId(),userId);
                        }
                    }
                }
            }
        }


        return cartInfoList;

    }


//删除未登录购物车数据
    @Override
    public void deleteCartList(String userTempId) {
        // 先删除表中的数据
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userTempId);
        cartInfoMapper.deleteByExample(example);

        // 删除缓存
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userTempId+CartConst.USER_CART_KEY_SUFFIX;
        jedis.del(cartKey);

        jedis.close();


    }

    @Override
    public void checkCart(String isChecked, String skuId, String userId) {
        // 修改数据 update cartInfo set is_checked = ? where userId = ? and skuId = ?\
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("skuId",skuId);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(isChecked);
        System.out.println("修改数据----");
        cartInfoMapper.updateByExampleSelective(cartInfo,example);

        // 第二种：按照缓存管理的原则：避免出现脏数据，先删除缓存，再放入缓存
        // 删除redis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        // 删除数据
        jedis.hdel(cartKey,skuId);

        // 放入缓存
        // select * from cartInfo where userId = ? and skuId = ?
        List<CartInfo> cartInfoList = cartInfoMapper.selectByExample(example);
        // 获取集合数据第一条数据
        if (cartInfoList!=null && cartInfoList.size()>0){
            CartInfo cartInfoQuery = cartInfoList.get(0);
            // 数据初始化实时价格！
            cartInfoQuery.setSkuPrice(cartInfoQuery.getCartPrice());
            jedis.hset(cartKey,skuId,JSON.toJSONString(cartInfoQuery));
        }
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {

        List<CartInfo> cartInfoList = new ArrayList<>();
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        // 获取数据
        List<String> cartList = jedis.hvals(cartKey);
        if (cartList!=null && cartList.size()>0){
            // 循环遍历
            for (String cartJson : cartList) {
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                if ("1".equals(cartInfo.getIsChecked())){
                    cartInfoList.add(cartInfo);
                }
            }
        }
        jedis.close();
        return cartInfoList;

    }


}
