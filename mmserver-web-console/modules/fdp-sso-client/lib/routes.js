var express = require('express');
var router = express.Router();
var oActions = require('./actions');

/*登录路由*/
router.get('/', oActions.fnGetView);

/*获取用户信息*/
router.get('/user', oActions.fnGetUser);

/*登出路由*/
router.get('/logout', oActions.fnDoLogout);

/*登录通知路由*/
router.get('/loginNotice', oActions.fnDoLoginVerify);

/*判断客户端是否与服务器端建立了关联*/
router.get('/sessionack', oActions.fnSessionAck);

/*实现与sso服务器关联session的回调路由*/
router.get('/sessionsyc', oActions.fnSessionSyc);

/*登出通知路由，尚未实现*/
router.get('/logoutNotice', oActions.fnDoLogoutVerify);

module.exports = router;