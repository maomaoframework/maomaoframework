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

(function(module, require, exports)
{
    var Ice = require("ice").Ice;
    var __M = Ice.__M;
    var Slice = Ice.Slice;

    var idl = __M.module("idl");

    idl.AppHashServerInfo = Slice.defineStruct(
        function(appId, ip, port, ssl)
        {
            this.appId = appId !== undefined ? appId : "";
            this.ip = ip !== undefined ? ip : "";
            this.port = port !== undefined ? port : 0;
            this.ssl = ssl !== undefined ? ssl : false;
        },
        true,
        function(__os)
        {
            __os.writeString(this.appId);
            __os.writeString(this.ip);
            __os.writeInt(this.port);
            __os.writeBool(this.ssl);
        },
        function(__is)
        {
            this.appId = __is.readString();
            this.ip = __is.readString();
            this.port = __is.readInt();
            this.ssl = __is.readBool();
        },
        7, 
        false);
    exports.idl = idl;
}
(typeof(global) !== "undefined" && typeof(global.process) !== "undefined" ? module : undefined,
 typeof(global) !== "undefined" && typeof(global.process) !== "undefined" ? require : this.Ice.__require,
 typeof(global) !== "undefined" && typeof(global.process) !== "undefined" ? exports : this));
