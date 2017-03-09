var express = require('express');
var router = express.Router();

var ConsoleActions = require('./console.actions.js');
var consoleActions = new ConsoleActions();

router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});


// app manager
router.get('/console/appmanager/app_install', function(req, res, next){
    res.render('console/appmanager/app_install');
});
router.get('/console/appmanager/app_detail', consoleActions.app_detail);
router.get('/console/appmanager/data_applist', consoleActions.data_applist);
router.post('/console/appmanager/data_commitInstance', consoleActions.data_commitInstance);
router.get('/console/appmanager/data_instancelist', consoleActions.data_instancelist);
router.get('/console/appmanager/data_undeploy', consoleActions.data_undeploy);
router.get('/console/appmanager/data_startup', consoleActions.data_startup);
router.get('/console/appmanager/data_shutdown', consoleActions.data_shutdown);

// ssh server manager
router.get('/console/server_manager/index', consoleActions.page_server_index);
router.get('/console/server_manager/data_loadServers', consoleActions.data_loadServers);
router.post('/console/server_manager/data_commitServer', consoleActions.data_commitServer);
router.get('/console/server_manager/data_removeServer', consoleActions.data_removeServer);

module.exports = router;
