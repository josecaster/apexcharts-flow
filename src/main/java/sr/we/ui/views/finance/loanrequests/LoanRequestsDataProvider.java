package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestVO;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class LoanRequestsDataProvider {

    public static CallbackDataProvider.CountCallback<LoanRequest, Void> count(LoanRequestVO vo) {
        return count -> {
            LoanRequestService exchangeRateService = ContextProvider.getBean(LoanRequestService.class);
            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            vo.setPageRequest(pageRequest);
            PagingResult<LoanRequest> list = exchangeRateService.list(vo.getToken(), vo);
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<LoanRequest, Void> fetch(LoanRequestVO vo) {
        return fetch -> {
            LoanRequestService exchangeRateService = ContextProvider.getBean(LoanRequestService.class);
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            PagingResult<LoanRequest> list = exchangeRateService.list(vo.getToken(), vo);
            return list.getResult().stream();
        };
    }

    /*public LoanRequestDataProvider(FetchCallback<LoanRequest, CurrencyExchnageFilter> fetchCallBack, CountCallback<LoanRequest, CurrencyExchnageFilter> countCallback, ValueProvider<LoanRequest, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
