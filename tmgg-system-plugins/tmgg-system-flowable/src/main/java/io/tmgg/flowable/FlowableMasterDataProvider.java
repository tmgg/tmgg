package io.tmgg.flowable;

import java.util.List;
import java.util.Map;

public interface FlowableMasterDataProvider {

    public String getUserNameById(String userId);

   default FlowableOrg getOrg(String unitId) {return null;}

   default List<String> getDirectChildUnitIdArr(String unitId) {return null;}

    Map<String,String> getUserMap();

}
