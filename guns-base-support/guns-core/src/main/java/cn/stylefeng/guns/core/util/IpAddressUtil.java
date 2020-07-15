/*
Copyright [2020] [https://www.stylefeng.cn]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Guns采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Guns源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/stylefeng/guns-separation
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/stylefeng/guns-separation
6.若您的项目无法满足以上几点，可申请商业授权，获取Guns商业授权许可，请在官网购买授权，地址为 https://www.stylefeng.cn
 */
package cn.stylefeng.guns.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.stylefeng.guns.core.consts.SymbolConstant;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 根据ip地址定位工具类，使用高德地地图定位api
 *
 * @author xuyuxiang
 * @date 2020/3/16 11:25
 */
public class IpAddressUtil {

    private static final Log log = Log.get();

    private static final String LOCAL_IP = "127.0.0.1";

    private static final String LOCAL_REMOTE_HOST = "0:0:0:0:0:0:0:1";

    private static final String URL = "https://restapi.amap.com/v3/ip";

    private static final String OUTPUT = "json";

    private static final String KEY = "c58799c473c985adf19f30437f12efee";

    private static final String PROVINCE = "province";

    private static final String CITY = "city";

    /**
     * 获取客户端ip
     *
     * @author xuyuxiang
     * @date 2020/3/19 9:32
     */
    public static String getIp(HttpServletRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            return LOCAL_IP;
        } else {
            String remoteHost = request.getRemoteHost();
            return LOCAL_REMOTE_HOST.equals(remoteHost) ? LOCAL_IP : remoteHost;
        }
    }

    /**
     * 根据ip地址定位
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:17
     */
    public static String getAddress(HttpServletRequest request) {
        String resultJson = SymbolConstant.DASH;

        String ip = getIp(request);

        //如果是本地ip或局域网ip，则直接不查询
        if (ObjectUtil.isEmpty(ip) || NetUtil.isInnerIP(ip)) {
            return resultJson;
        }

        try {
            //根据url获取地址
            resultJson = HttpUtil.get(URL, genParamMap(ip));
        } catch (Exception e) {
            log.error(">>> 根据ip定位异常：{}", e.getMessage());
            return resultJson;
        }
        if (ObjectUtil.isEmpty(resultJson)) {
            return resultJson;
        } else {
            Object provinceObj = JSON.parseObject(resultJson).get(PROVINCE);
            Object cityObj = JSON.parseObject(resultJson).get(CITY);

            if (ObjectUtil.hasEmpty(provinceObj, cityObj)) {
                return resultJson;
            }

            String province = provinceObj.toString();
            String city = cityObj.toString();
            //拼接 省+市 并返回
            return province.equals(city) ? province : province + city;
        }
    }

    /**
     * 构造map参数
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:17
     */
    private static Map<String, Object> genParamMap(String ip) {
        Map<String, Object> paramMap = CollectionUtil.newHashMap();
        paramMap.put("ip", ip);
        paramMap.put("output", OUTPUT);
        paramMap.put("key", KEY);
        return paramMap;
    }

}
