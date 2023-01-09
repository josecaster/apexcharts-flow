package sr.we.ui.views.finance.loans;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.LoanVO;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class LoanDataProvider {

    public static CallbackDataProvider.CountCallback<Loan, Void> count(LoanVO vo) {
        return count -> {
            LoanService exchangeRateService = ContextProvider.getBean(LoanService.class);
            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            vo.setPageRequest(pageRequest);
            PagingResult<Loan> list = exchangeRateService.list(AuthenticatedUser.token(),vo);
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<Loan, Void> fetch(LoanVO vo) {
        return fetch -> {
            LoanService exchangeRateService = ContextProvider.getBean(LoanService.class);
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            PagingResult<Loan> list = exchangeRateService.list(AuthenticatedUser.token(),vo);
            return list.getResult().stream();
        };
    }

    /*public LoanDataProvider(FetchCallback<Loan, CurrencyExchnageFilter> fetchCallBack, CountCallback<Loan, CurrencyExchnageFilter> countCallback, ValueProvider<Loan, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
