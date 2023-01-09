package sr.we.ui.views.invoice;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.InvoiceVO;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class InvoiceDataProvider {

    public static CallbackDataProvider.CountCallback<Invoice, Void> count(InvoiceVO vo) {
        return count -> {
            InvoiceService exchangeRateService = ContextProvider.getBean(InvoiceService.class);
            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            vo.setPageRequest(pageRequest);
            PagingResult<Invoice> list = exchangeRateService.list(vo.getToken(), vo);
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<Invoice, Void> fetch(InvoiceVO vo) {
        return fetch -> {
            InvoiceService exchangeRateService = ContextProvider.getBean(InvoiceService.class);
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            PagingResult<Invoice> list = exchangeRateService.list(vo.getToken(), vo);
            return list.getResult().stream();
        };
    }

    /*public InvoiceDataProvider(FetchCallback<Invoice, CurrencyExchnageFilter> fetchCallBack, CountCallback<Invoice, CurrencyExchnageFilter> countCallback, ValueProvider<Invoice, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
