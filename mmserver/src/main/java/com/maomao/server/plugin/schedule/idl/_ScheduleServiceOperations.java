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
// Generated from file `ScheduleService.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package com.maomao.server.plugin.schedule.idl;

public interface _ScheduleServiceOperations
{
    /**
     * add a schedule to schedule center
     * @param __current The Current object for the invocation.
     **/
    String registSchedule(String connectionUrl, String serviceName, String cronExpress, boolean imediate, Ice.Current __current);

    /**
     * execute schedule
     * @param __current The Current object for the invocation.
     **/
    String executeSchedule(String serviceName, Ice.Current __current);
}
