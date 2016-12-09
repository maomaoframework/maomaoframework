var express = require('express');
var router = express.Router();

var oActions = require('./actions');

/*获取登录界面及执行登录操作*/
router.get('/', oActions.fnGetLoginPage);
router.post('/', oActions.fnDoLogin);

/*执行登出操作的路由*/
router.get('/logout', oActions.fnDoLogout);

/*验证token，给验证方返回用户信息*/
router.put('/verifytoken', oActions.fnVerifyToken);

/*实现与sso客户端关联session的路由*/
router.get('/sessionsyc', oActions.fnSessionSyc);

module.exports = router;