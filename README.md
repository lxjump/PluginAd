## 插件化广告SDK
### 目标
Android 接入广告SDK包含很多资源和四大组件相关的逻辑,实现成把广告SDK接入并编译成插件apk,并由宿主直接动态加载,从而使宿主应用和SDK解耦合，不需要在宿主App注册一堆广告SDK的Activity、service等四大组件
该Demo是以vivo的广告SDK为例

<img src="./images/plugin_ads_sdk.png" style="zoom:50%;" />

## 介绍

##### 项目模块

1. adsdk Module，接入平台广告SDK，封装好方法供宿主apk调用，并编译成apk文件

2. loader Module

   a. 获取存储在asset目录下的adsdk Module apk文件，解压并动态加载adsdk Modeul apk文件（dex & so）

   b. 通过反射adsdk Module封装的方法，调用广告sdk各类广告的方法实现播放

##### 插件启动Activity的流程

1. 动态代理ActivityTaskManager，实现拦截startActivity方法，替换ActivityThread的mH(ActivityThread的Handler实例)字段，用于拦截应用运行时四大组件间的事件
2. 在startActivity判断启动的Activity是否是插件中的Activity，是的话就执行替换Intent，把启动的Activity(插件的Activity，未在AndroidManifest中注册)替换成在AndroidManifest.xml注册的代理Activity
3. 在mH拦截到EXECUTE_TRANSACTION消息时(Android处理Activity启动的消息，这个时候已经过了Android系统对Activity是否在AndroidManifest.xml注册的验证)进行还原，把原来要启动的插件Activity替换回去

### 资料

[Tencent Shadow框架](https://github.com/Tencent/Shadow )

[Didi VirtualAPK](https://github.com/didi/VirtualAPK )

> [!IMPORTANT]
>
> 问题
>
> 1. 广告SDK聚合了其他广告SDK,包含多种Activity和资源,需要处理好不同广告sdk的区别
> 2. 广告sdk除了Activity,还包含其他service、provider等四大组件,用于播放广告出发下载等逻辑，也需要处理对应的生命周期
> 3. 插件化用到反射、动态代理，存在着不同android系统版本、不同厂商rom的适配问题，目前只测试了android 10、14、15的手机

