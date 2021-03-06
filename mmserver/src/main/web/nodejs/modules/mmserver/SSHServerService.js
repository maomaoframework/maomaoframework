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
// Generated from file `SSHServerService.ice'
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

    /**
     * App服务
     **/
    idl.SSHServerService = Slice.defineObject(
        undefined,
        Ice.Object, undefined, 1,
        [
            "::Ice::Object",
            "::idl::SSHServerService"
        ],
        -1, undefined, undefined, false);

    idl.SSHServerServicePrx = Slice.defineProxy(Ice.ObjectPrx, idl.SSHServerService.ice_staticId, undefined);

    Slice.defineOperations(idl.SSHServerService, idl.SSHServerServicePrx,
    {
        "loadServers": [, , , , , [7], , , , , ],
        "addServer": [, , , , , [7], [[7]], , , , ],
        "updateServer": [, , , , , [7], [[7], [7]], , , , ],
        "removeServer": [, , , , , [7], [[7]], , , , ]
    });
    exports.idl = idl;
}
(typeof(global) !== "undefined" && typeof(global.process) !== "undefined" ? module : undefined,
 typeof(global) !== "undefined" && typeof(global.process) !== "undefined" ? require : this.Ice.__require,
 typeof(global) !== "undefined" && typeof(global.process) !== "undefined" ? exports : this));
