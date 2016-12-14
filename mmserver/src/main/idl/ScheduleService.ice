[["java:package:com.maomao.server.plugin.schedule"]]
module idl {
	interface ScheduleService {
		/**
		 * add a schedule to schedule center
		 */
	    string registSchedule(string connectionUrl , string serviceName, string cronExpress, bool imediate);
	    
	    /**
	     * execute schedule
	     */
	    string executeSchedule(string serviceName);
	};
};