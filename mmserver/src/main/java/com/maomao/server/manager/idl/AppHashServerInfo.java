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
// Generated from file `AppHashServerInfo.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package com.maomao.server.manager.idl;

public class AppHashServerInfo implements java.lang.Cloneable, java.io.Serializable
{
    public String appId;

    public String ip;

    public int port;

    public boolean ssl;

    public AppHashServerInfo()
    {
        appId = "";
        ip = "";
    }

    public AppHashServerInfo(String appId, String ip, int port, boolean ssl)
    {
        this.appId = appId;
        this.ip = ip;
        this.port = port;
        this.ssl = ssl;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        AppHashServerInfo _r = null;
        if(rhs instanceof AppHashServerInfo)
        {
            _r = (AppHashServerInfo)rhs;
        }

        if(_r != null)
        {
            if(appId != _r.appId)
            {
                if(appId == null || _r.appId == null || !appId.equals(_r.appId))
                {
                    return false;
                }
            }
            if(ip != _r.ip)
            {
                if(ip == null || _r.ip == null || !ip.equals(_r.ip))
                {
                    return false;
                }
            }
            if(port != _r.port)
            {
                return false;
            }
            if(ssl != _r.ssl)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 5381;
        __h = IceInternal.HashUtil.hashAdd(__h, "::idl::AppHashServerInfo");
        __h = IceInternal.HashUtil.hashAdd(__h, appId);
        __h = IceInternal.HashUtil.hashAdd(__h, ip);
        __h = IceInternal.HashUtil.hashAdd(__h, port);
        __h = IceInternal.HashUtil.hashAdd(__h, ssl);
        return __h;
    }

    public AppHashServerInfo
    clone()
    {
        AppHashServerInfo c = null;
        try
        {
            c = (AppHashServerInfo)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString(appId);
        __os.writeString(ip);
        __os.writeInt(port);
        __os.writeBool(ssl);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        appId = __is.readString();
        ip = __is.readString();
        port = __is.readInt();
        ssl = __is.readBool();
    }

    static public void
    __write(IceInternal.BasicStream __os, AppHashServerInfo __v)
    {
        if(__v == null)
        {
            __nullMarshalValue.__write(__os);
        }
        else
        {
            __v.__write(__os);
        }
    }

    static public AppHashServerInfo
    __read(IceInternal.BasicStream __is, AppHashServerInfo __v)
    {
        if(__v == null)
        {
             __v = new AppHashServerInfo();
        }
        __v.__read(__is);
        return __v;
    }
    
    private static final AppHashServerInfo __nullMarshalValue = new AppHashServerInfo();

    public static final long serialVersionUID = 72184974080619250L;
}
