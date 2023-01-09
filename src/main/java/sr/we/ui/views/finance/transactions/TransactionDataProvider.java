package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class TransactionDataProvider {

    public static CallbackDataProvider.CountCallback<PaymentTransaction, Void> count(PaymentTransactionVO vo) {
        return count -> {

            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            vo.setPageRequest(pageRequest);
            PaymentTransactionService customerService = ContextProvider.getBean(PaymentTransactionService.class);
            PagingResult<PaymentTransaction> list = customerService.list(vo.getToken(), vo);
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<PaymentTransaction, Void> fetch(PaymentTransactionVO vo) {
        return fetch -> {
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            PaymentTransactionService customerService = ContextProvider.getBean(PaymentTransactionService.class);
            PagingResult<PaymentTransaction> list = customerService.list(vo.getToken(), vo);
            return list.getResult().stream();
        };
    }


    /*public CurrencyExchangeDataProvider(FetchCallback<CurrencyExchange, CurrencyExchnageFilter> fetchCallBack, CountCallback<CurrencyExchange, CurrencyExchnageFilter> countCallback, ValueProvider<CurrencyExchange, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
