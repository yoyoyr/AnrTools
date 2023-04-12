设计思路：
    MessageMonitor监控存储主线程消息的相关信息。并有checkMainRunnable定时任务判断主线程处理消息的能力。


报文说明：
    messageSamplerCache=[//主线程所有消息
        MessageListBean{
            msgType=INFO, //MSG_TYPE_INFO 300ms以下。MSG_TYPE_WARN 300ms-3000ms MSG_TYPE_ANR 3000ms以上 MSG_TYPE_GAP 两个消息间隔超过300ms MSG_TYPE_ACTIVITY_THREAD_H  ActivityThread.H的消息
            count=2, //聚合消息个数
            wallTime=303, //总时间，为上一个消息包结束时间-这个消息包结束时间。所以会包含等待消息的时间
            cpuTime=10, //执行的cpu事件，不包含等待锁的时间
            boxMessages=[
                MessageBean{
                    handleName=android.app.ActivityThread$H,//Handler的类名
                    handlerAddress=1fdfe62, //内存地址
                    callbackName= null, //回调，如Runnable的类名
                    messageWhat=164, //对应Message的what字段
                    monitorMsgId=2 //MessageMonitor内部的id字段，可视为消息的顺序
                }
                MessageBean{
                    handleName=android.os.Handler,
                    handlerAddress=97da18e,
                    callbackName= com.tools.anrtools.MainActivity$$ExternalSyntheticLambda3@39049f6,
                    messageWhat=0,
                    monitorMsgId=3
                },
            ],
        },
    ]


    scheduledSamplerCache=[//调度消息，用于判断主线程的消息处理能力
        ScheduledInfo(
            overTime=1, //消息处理总时间超过300ms的数额，也就是301ms.时间 = 消息处理完的时间 - 消息加入消息队列的时间 - 300
            monitorMsgId=29,
            isDone=false//消息是否处理完成
            ),
        ScheduledInfo(dealt=1, monitorMsgId=33, isStart=false),
    ],


    messageQueueSample={//loop.dump()
        Looper (main, tid 2) {5225ffc}
        Message 0: {
            when=-1s63ms //消息计划执行时间。-1s表示按计划1s前就应该执行。1s表示1s后执行
            callback=android.view.View$UnsetPressedState //Runnable回调的类名
            target=android.view.ViewRootImpl$ViewRootHandler //发送消息的handler
            }
        Message 1: { when=-40ms callback=android.view.Choreographer$FrameDisplayEventReceiver target=android.view.Choreographer$FrameHandler }
        Message 2: { when=-28ms barrier=131 }
        Message 3: { when=+288ms callback=com.anr.tools.MessageMonitor$checkMainRunnable$1 target=android.os.Handler }
        (Total messages: 4, polling=false, quitting=false)
        Dump time: 2023-04-12 13:59:45.637 GMT+08:00

        Package: com.tools.anrtools

        Current looper: Looper (main, tid=23358)

        Current running message:

        seq=342 //消息编号，可能是整个系统的
        plan=13:59:44.573 //计划执行时间
        late=1054ms //执行延迟了多久
        h=android.view.ViewRootImpl$ViewRootHandler  //发送消息的对象
        c=android.view.View$PerformClick //回调的类名

        History of long time messages on Looper (main, tid=23358): //执行时间过长的历史消息

        Msg #1: seq=336 plan=13:59:43.024 late=2058ms wall=505ms running=0ms h=android.os.Handler c=com.tools.anrtools.MainActivity$$ExternalSyntheticLambda3

        Msg #2: seq=335 plan=13:59:43.024 late=1551ms wall=505ms running=0ms h=android.os.Handler c=com.tools.anrtools.MainActivity$$ExternalSyntheticLambda3

        Msg #3: seq=334 plan=13:59:43.024 late=1031ms wall=504ms running=0ms h=android.os.Handler c=com.tools.anrtools.MainActivity$$ExternalSyntheticLambda3

        Msg #4: seq=333 plan=13:59:43.024 late=527ms wall=503ms running=0ms h=android.os.Handler c=com.tools.anrtools.MainActivity$$ExternalSyntheticLambda3

        Msg #5: seq=332 plan=13:59:43.024 late=17ms wall=508ms running=0ms h=android.os.Handler c=com.tools.anrtools.MainActivity$$ExternalSyntheticLambda3

        Msg #6: seq=1 plan=13:59:14.166 late=3ms wall=337ms running=0ms h=android.app.ActivityThread$H w=162)
    }


