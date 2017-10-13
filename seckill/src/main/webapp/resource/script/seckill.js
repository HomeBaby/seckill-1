//javascript 模块化

var seckill = {

    //封装秒杀相关的url

    URL: {
        getExposerUrl: function (seckillId) {
            return seckill.URL.getROOT() + "/seckill/" + seckillId + "/exposer";
        },
        getTimeUrl: function () {
            return seckill.URL.getROOT() + "/seckill/getTime";
        },
        geExecutionUrl: function (seckillId, md5, userPhone) {
            return seckill.URL.getROOT() + "/seckill/" + seckillId + "/" + md5 + "/execution?userPhone=" + userPhone;
        },
        getROOT: function () {
            return $("#rootPath").val();
        }
    },
    //详细秒杀业务处理
    detail: {
        //详情页初始化

        //验证手机号
        validatePhone: function (phone) {
            if (phone != undefined && phone != null && phone.length == 11 && !isNaN(phone)) {
                return true;
            } else {
                return false;
            }
        },
        countDown: function (seckillId, startTime, endTime, nowTime) {

            var seckillBox = $('#seckill-box');

            //秒杀结束
            if (nowTime > endTime) {
                seckillBox.html('秒杀已经结束');
                //秒杀未开始
            } else if (nowTime < startTime) {

                var killTime = new Date(startTime + 1000);
                seckillBox.countdown(killTime, function (event) {
                    var format = event.strftime('秒杀倒计时：%D天  %H时  %M分  %S秒');
                    seckillBox.html(format);
                }).on('finish.countdown', function () {
                    seckill.detail.handleSeckill(seckillId, seckillBox);
                });
                //正在进行秒杀
            } else {
                seckill.detail.handleSeckill(seckillId, seckillBox);
            }
        },


        handleSeckill: function (seckillId, node) {

            console.log("执行秒杀处理方法");

            node.hide().html('<button class="btn btn-info" id="beginKillBtn">立即抢购</button>');

            //获取秒杀地址
            $.post(seckill.URL.getExposerUrl(seckillId), {}, function (result) {
                if (result && result['success']) {
                    var exposer = result['data'];
                    console.log(result);

                    if (exposer['exposed']) {

                        var seckillId = exposer['seckillId'];

                        var md5 = exposer['md5'];

                        $("#beginKillBtn").one('click', function () {

                            $("#beginKillBtn").addClass("disable");

                            $.post(seckill.URL.geExecutionUrl(seckillId, md5, $.cookie('killPhone')), {}, function (result) {
                                if (result && result['success']) {

                                    var killResult = result['data'];
                                    var state = killResult['state'];
                                    var stateInfo = killResult['stateInfo'];

                                    node.html('<span class="label label-success">' + stateInfo + '</span>');

                                }
                            });
                        });
                        node.show();
                    } else {
                        var seckillId = exposer['seckillId'];
                        var now = exposer['now'];
                        var startTime = exposer['startTime'];
                        var endTime = exposer['endTime'];

                        seckill.detail.countDown(seckillId, startTime, endTime, now);
                    }
                }
            })

        },
        init: function (params) {
            var killPhone = $.cookie('killPhone');
            var seckillId = params['seckillId'];


            //进行手机号验证
            if (!seckill.detail.validatePhone(killPhone)) {
                var killPhoneModal = $("#killPhoneModal");
                //显示弹出层
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false //关闭键盘事件
                });

                //对button添加点击事件
                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhone").val();
                    if (seckill.detail.validatePhone(inputPhone)) {

                        //验证通过，写入phone到cookie中
                        $.cookie('killPhone', inputPhone, {expires: 7, path: "/seckill"});
                        window.location.reload();
                    } else {
                        $("#killPhoneMessage").hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }

            var startTime = params['startTime'];
            var endTime = params['endTime'];
            //用户已经登录
            $.get(seckill.URL.getTimeUrl(), {}, function (result) {
                if (result['success'] == true) {
                    var nowTime = result['data'];
                    console.log(nowTime);

                    seckill.detail.countDown(seckillId, startTime, endTime, nowTime);
                } else {
                    console.log(result);
                }
            });

        }
    }

}