// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************
//
// Ice version 3.6.3
//
// <auto-generated>
//
// Generated from file `AppService.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package com.maomao.server.manager.idl;

/**
 * App服务
 **/
public interface _AppServiceOperationsNC
{
    void stopServer();

    /**
     * 返回全部App
     **/
    java.util.List<App> loadApps();

    /**
     * 停止一个App的某一个实例
     * instanceId 传递为空时，表示停止该App的全部实例
     **/
    String stopAppInstance(String appId, String instanceId);

    /**
     * 停止一个App的某一个实例
     * instanceId 传递为空时，表示停止该App的全部实例
     **/
    String restartAppInstance(String appId, String instanceId);

    /**
     * 启动一个App实例
     * instanceId为空时,表示启动该App的全部实例
     **/
    String startAppInstance(String appId, String instanceId);

    /**
     * 删除一个App实例
     **/
    String removeAppInstance(String appId, String instanceId);

    /**
     * Create a new app
     **/
    String createApp(String appId);

    /**
     * 删除一个App
     **/
    String removeApp(String appId);

    /**
     * 停止一个App及其所有实例
     **/
    String stopApp(String appId);

    /**
     * 启动App的所有实例
     **/
    String startApp(String appId);

    /**
     * 重启一个应用的所有实例
     **/
    String restartApp(String appId);

    /**
     * 创建一个新的App实例
     **/
    String createAppInstance(String appId, String appInstanceJson);

    /**
     * 更新一个现有的App实例
     **/
    String updateAppInstance(String appId, String instanceId, String appInstanceJson);

    /**
     * 同步服务器信息
     **/
    String syncServerInfo();

    /**
     * 　返回服务器信息
     **/
    String getHdpServerInfo();

    /**
     * 　app与hdpserver保持同步状态
     **/
    String appSyncStatus(String jsonInfo, String ip, int port);

    /**
     * app停止时发送给hdp server的通知事件
     **/
    String appStopNotify(String appId, String ip, int port);

    /**
     * app启动完毕后，发送通知
     **/
    String appStartupNotify(String appId, String ip, int port);

    /**
     * 迫使服务器关闭
     **/
    void forceAppInstanceShutdown(int seconds);

    /**
     * 迫使服务器关闭
     **/
    void forceAppInstanceRestart(int seconds);
}
