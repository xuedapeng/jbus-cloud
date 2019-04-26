package cloud.jbus.logic.share;


import cloud.jbus.common.constant.StatusConst;
import cloud.jbus.common.helper.NumericHelper;
import cloud.jbus.logic.device.param.IPageParam;
import fw.jbiz.ext.json.ZSimpleJsonObject;

public class ParamValidator {

	public static boolean checkPageParam(IPageParam myParam, ZSimpleJsonObject res) {

		
		if (myParam.getPage() == null) {
			myParam.setPage(String.valueOf(StatusConst.DEFAULT_PAGE_IDX));
		}
		if (myParam.getPageSize() == null) {
			myParam.setPageSize(String.valueOf(StatusConst.DEFAULT_PAGE_SIZE));
		}
		
		if (!NumericHelper.isInteger(myParam.getPage())
				|| !NumericHelper.isInteger(myParam.getPageSize())) {
			
			res.clear();
			res.add("status", -3)
				.add("msg", "invalid page info.");
			
			return false;
		}
		
		return true;
	}
}
