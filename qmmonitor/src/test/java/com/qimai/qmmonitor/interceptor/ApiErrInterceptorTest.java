package com.qimai.qmmonitor.interceptor;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiErrInterceptorTest {

    @Test
    public void setLevel() {
        String text = "{\"trace_id\":\"9c963d172eff63c0126d846d4dc64f88\",\"data\":{\"items\":[{\"terminal_id\":\"10686990\",\"access_token\":\"648406f67a1a4ea18ef75d8be35a77a7\",\"store_id\":1000185,\"id\":0,\"sub_store_id\":0,\"terminal_name\":\"默认终端\"},{\"terminal_id\":\"11420227\",\"updated_at\":\"2019-10-18 09:52:45\",\"id\":86,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"976c7ad84f1f45468e34bd3306d50928\",\"store_id\":1000185,\"terminal_name\":\"王雷\",\"name\":\"王雷\",\"created_at\":\"2019-10-18 09:52:45\"},{\"terminal_id\":\"1231433424324\",\"updated_at\":\"2019-07-11 15:13:11\",\"id\":65,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"1231433424324\",\"store_id\":1000185,\"terminal_name\":\"默认终端\",\"name\":\"\",\"created_at\":\"2019-05-14 14:08:54\"},{\"terminal_id\":\"10949179\",\"updated_at\":\"2019-03-18 13:44:36\",\"id\":60,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"bb5d9778a13e4f66bba4d26bc6b1dea3\",\"store_id\":1000185,\"terminal_name\":\"阿斯顿飞过\",\"name\":\"阿斯顿飞过\",\"created_at\":\"2019-03-18 13:44:36\"},{\"terminal_id\":\"10949141\",\"updated_at\":\"2019-03-18 13:34:42\",\"id\":58,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"f0dea66ccdf84e5694ad9c17e058d201\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 13:34:42\"},{\"terminal_id\":\"10949130\",\"updated_at\":\"2019-03-18 13:32:46\",\"id\":57,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"b800f3ccf4774735b718353556d703a7\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 13:32:46\"},{\"terminal_id\":\"10949092\",\"updated_at\":\"2019-03-18 13:24:54\",\"id\":56,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"c8e7c83e06ed42c0b3ae95d03414a7eb\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 13:24:54\"},{\"terminal_id\":\"10949086\",\"updated_at\":\"2019-03-18 13:23:28\",\"id\":55,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"8d21ed4497ec4c09af99569e38b42b97\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 13:23:28\"},{\"terminal_id\":\"10948805\",\"updated_at\":\"2019-03-18 11:56:36\",\"id\":53,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"c8da8bb273e24dc99304fa5cf2248d92\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 11:56:36\"},{\"terminal_id\":\"10948803\",\"updated_at\":\"2019-03-18 11:56:02\",\"id\":52,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"019f117216544542b38851c2ff7909a6\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 11:56:02\"},{\"terminal_id\":\"10948802\",\"updated_at\":\"2019-03-18 11:55:52\",\"id\":51,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"dde21cae08484780995bcb65316f3bd2\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 11:55:52\"},{\"terminal_id\":\"10948788\",\"updated_at\":\"2019-03-18 11:53:30\",\"id\":50,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"80f21fe5953f42c3a742cbbfef1565c2\",\"store_id\":1000185,\"terminal_name\":\"1000185\",\"name\":\"1000185\",\"created_at\":\"2019-03-18 11:53:30\"},{\"terminal_id\":\"10832435\",\"updated_at\":\"2019-01-15 16:11:10\",\"id\":47,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"fa2fdceb40454ad781aa6a16b11e45ae\",\"store_id\":1000185,\"terminal_name\":\"6\",\"name\":\"6\",\"created_at\":\"2019-01-15 16:11:10\"},{\"terminal_id\":\"10832433\",\"updated_at\":\"2019-01-15 16:11:05\",\"id\":46,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"a3b062bca2c548aaabf483a697a20c0a\",\"store_id\":1000185,\"terminal_name\":\"5\",\"name\":\"5\",\"created_at\":\"2019-01-15 16:11:05\"},{\"terminal_id\":\"10832432\",\"updated_at\":\"2019-01-15 16:11:00\",\"id\":45,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"dd5303e8a87e4002875530faeedbe105\",\"store_id\":1000185,\"terminal_name\":\"4\",\"name\":\"4\",\"created_at\":\"2019-01-15 16:11:00\"},{\"terminal_id\":\"10832430\",\"updated_at\":\"2019-01-15 16:10:55\",\"id\":44,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"990f824e99994108af07f8464bf87d8e\",\"store_id\":1000185,\"terminal_name\":\"3\",\"name\":\"3\",\"created_at\":\"2019-01-15 16:10:55\"},{\"terminal_id\":\"10832429\",\"updated_at\":\"2019-01-15 16:10:48\",\"id\":43,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"d37b5177024947b09b5410ff6c36df68\",\"store_id\":1000185,\"terminal_name\":\"12\",\"name\":\"12\",\"created_at\":\"2019-01-15 16:10:48\"},{\"terminal_id\":\"10832214\",\"updated_at\":\"2019-01-15 15:07:20\",\"id\":38,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"3e06af3b45f447c3ba29092a1b1ad888\",\"store_id\":1000185,\"terminal_name\":\"王雷\",\"name\":\"王雷\",\"created_at\":\"2019-01-15 15:07:20\"},{\"terminal_id\":\"10793501\",\"updated_at\":\"2018-12-25 17:31:48\",\"id\":29,\"terminal_type\":1,\"sub_store_id\":0,\"top_id\":0,\"access_token\":\"411d753b45b340628a0cc39b2ddd28bf\",\"store_id\":1000185,\"terminal_name\":\"1\",\"name\":\"1\",\"created_at\":\"2018-12-25 17:31:48\"}]},\"status\":true,\"message\":\"终端列表\",\"code\":2300000200}";

     //   boolean status = text.contains("status:Boolean");

    }
}