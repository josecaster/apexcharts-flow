package sr.we.ui.views.customers;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.CurrencyExchange;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.CustomerVO;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class CustomerDataProvider {

    public static CallbackDataProvider.CountCallback<Customer, Void> count(CustomerVO vo) {
        return count -> {

//            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(0, 1) : PageRequestImpl.of(0, 1, SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            CustomerService customerService = ContextProvider.getBean(CustomerService.class);
            PagingResult<Customer> list = customerService.list(vo, vo.getToken());
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<Customer, Void> fetch(CustomerVO vo) {
        return fetch -> {
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            CustomerService customerService = ContextProvider.getBean(CustomerService.class);
            PagingResult<Customer> list = customerService.list(vo, vo.getToken());
            return list.getResult().stream();
        };
    }

    /*public CurrencyExchangeDataProvider(FetchCallback<CurrencyExchange, CurrencyExchnageFilter> fetchCallBack, CountCallback<CurrencyExchange, CurrencyExchnageFilter> countCallback, ValueProvider<CurrencyExchange, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
