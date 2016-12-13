[["java:package:com.maomao.server.plugin.schedule"]]
module idl {
	interface ScheduleService {
		/**
		 * add a schedule to schedule center
		 */
	    string registSchedule(string connectionUrl , string serviceName, string cronExpress);
	    
	    /**
	     * execute schedule
	     */
	    string executeSchedule(string serviceName);
	};
};