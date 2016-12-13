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

public final class ScheduleServiceHolder extends Ice.ObjectHolderBase<ScheduleService>
{
    public
    ScheduleServiceHolder()
    {
    }

    public
    ScheduleServiceHolder(ScheduleService value)
    {
        this.value = value;
    }

    public void
    patch(Ice.Object v)
    {
        if(v == null || v instanceof ScheduleService)
        {
            value = (ScheduleService)v;
        }
        else
        {
            IceInternal.Ex.throwUOE(type(), v);
        }
    }

    public String
    type()
    {
        return _ScheduleServiceDisp.ice_staticId();
    }
}
