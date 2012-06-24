package net.ion.amazon.common;

import com.amazonaws.services.ec2.model.Region;

public class ServiceRegion {

	public final static class RegionName {
		public final static String UsEastVerginia = "US East (Northern Virginia) Region" ;
		public final static String UsWestOregon = "US West (Oregon) Region" ;
		public final static String UsWestCalifonia = "US West (Northern California) Region" ;
		public final static String EUIreland = "EU (Ireland) Region" ;
		public final static String AsiaSingapore = "Asia Pacific (Singapore) Region" ;
		public final static String AsiaTokyo = "Asia Pacific (Tokyo) Region" ;
		public final static String SouthAmericaSaoPaulo = "South America (Sao Paulo) Region" ;
	}
	
	private final static class EndPointPostfix {
		public final static String UsEastVerginia = ".us-east-1.amazonaws.com" ;
		public final static String UsWestOregon = ".us-west-2.amazonaws.com" ;
		public final static String UsWestCalifonia = ".us-west-1.amazonaws.com" ;
		public final static String EUIreland = ".eu-west-1.amazonaws.com" ;
		public final static String AsiaSingapore = ".ap-southeast-1.amazonaws.com" ;
		public final static String AsiaTokyo = ".ap-northeast-1.amazonaws.com" ;
		public final static String SouthAmericaSaoPaulo = ".sa-east-1.amazonaws.com" ;
	}
	

	public final static class SQS {
		private static String ServiceName = "sqs" ;
		
		public final static ServiceRegion USEastVerginia = ServiceRegion.create(RegionName.UsEastVerginia, ServiceName + EndPointPostfix.UsEastVerginia) ;
		public final static ServiceRegion UsWestOregon = ServiceRegion.create(RegionName.UsWestOregon, ServiceName + EndPointPostfix.UsWestOregon) ;
		public final static ServiceRegion UsWestCalifonia = ServiceRegion.create(RegionName.UsWestCalifonia, ServiceName + EndPointPostfix.UsWestCalifonia) ;
		public final static ServiceRegion EUIreland = ServiceRegion.create(RegionName.EUIreland, ServiceName + EndPointPostfix.EUIreland) ;
		public final static ServiceRegion AsiaSingapore = ServiceRegion.create(RegionName.AsiaSingapore, ServiceName + EndPointPostfix.AsiaSingapore) ;
		public final static ServiceRegion AsiaTokyo = ServiceRegion.create(RegionName.AsiaTokyo, ServiceName + EndPointPostfix.AsiaTokyo) ;
		public final static ServiceRegion SouthAmericaSaoPaulo = ServiceRegion.create(RegionName.SouthAmericaSaoPaulo, ServiceName + EndPointPostfix.SouthAmericaSaoPaulo) ;

	}
	
	
	
	
	private Region region ;
	private ServiceRegion(Region region) {
		this.region = region ;
	}

	private final static ServiceRegion create(String regionName, String endpoint){
		return new ServiceRegion(new Region().withRegionName(regionName).withEndpoint(endpoint)) ;
	}

	public String getEndPoint() {
		return region.getEndpoint();
	}
	
	
}
