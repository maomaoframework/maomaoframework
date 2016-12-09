var express = require("express");
var router = express.Router();

var HdpAction = require('./hdp.actions.js');


// 获取一个用列表
var hdpAction = new HdpAction();
router.get('/appmanager/app_detail', hdpAction.app_detail);

router.get('/appmanager/data_applist', hdpAction.data_applist);
router.post('/appmanager/data_commitInstance', hdpAction.data_commitInstance);
router.get('/appmanager/data_instancelist', hdpAction.data_instancelist);
router.get('/appmanager/data_undeploy', hdpAction.data_undeploy);
router.get('/appmanager/data_startup', hdpAction.data_startup);
router.get('/appmanager/data_shutdown', hdpAction.data_shutdown);

// 服务器管理主界面
router.get('/server_manager/index', hdpAction.page_server_index);
router.get('/server_manager/data_loadServers', hdpAction.data_loadServers);
router.post('/server_manager/data_commitServer', hdpAction.data_commitServer);
router.get('/server_manager/data_removeServer', hdpAction.data_removeServer);

module.exports = router;
