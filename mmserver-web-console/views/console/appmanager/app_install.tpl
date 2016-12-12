<!-- top tiles -->
<div id="singlepage">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
              <div class="x_title">
                <h2>安装新的应用</h2>
                <div class="clearfix"></div>
              </div>
              <div class="x_content">
                <form class="form-horizontal form-label-left" novalidate="">
                  <div class="item form-group">
                    <label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">上传应用 <span class="required">*</span></label>
                    <div class="col-md-6 col-sm-6 col-xs-12">
                      <input type="file" name="appfile" id="appfile" required="required" >
                    </div>
                  </div>
                  <div class="item form-group">
                    <label class="control-label col-md-3 col-sm-3 col-xs-12" for="email">立即启动 <span class="required">*</span></label>
                    <div class="col-md-6 col-sm-6 col-xs-12">
                       <div id="gender" class="btn-group" data-toggle="buttons">
                            <label class="btn btn-default" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                              <input type="radio" name="autoEnable" value="1"> &nbsp; 是 &nbsp;
                            </label>
                            <label class="btn btn-default" data-toggle-class="btn-primary" data-toggle-passive-class="btn-default">
                              <input type="radio" name="autoEnabble" value="0"> &nbsp; 否 &nbsp;
                            </label>
                        </div> 
                    </div>
                  </div>
                  <div class="ln_solid"></div>
                  <div class="form-group">
                    <div class="col-md-6 col-md-offset-3">
                      <button type="submit" class="btn btn-primary">取消</button>
                      <button id="send" type="submit" class="btn btn-success">提交</button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
        </div>
    </div>
</div>

