package sr.we.ui.views.pos;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.PosHeaderService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.PosHeaderVO;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class TicketsDataProvider {

    public static CallbackDataProvider.CountCallback<PosHeader, Void> count(PosHeaderVO vo) {
        return count -> {
            PosHeaderService exchangeRateService = ContextProvider.getBean(PosHeaderService.class);
            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            vo.setPageRequest(pageRequest);
            PagingResult<PosHeader> list = exchangeRateService.list(vo, vo.getToken());
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<PosHeader, Void> fetch(PosHeaderVO vo) {
        return fetch -> {
            PosHeaderService exchangeRateService = ContextProvider.getBean(PosHeaderService.class);
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            PagingResult<PosHeader> list = exchangeRateService.list(vo, vo.getToken());
            return list.getResult().stream();
        };
    }

    /*public PosHeaderDataProvider(FetchCallback<PosHeader, CurrencyExchnageFilter> fetchCallBack, CountCallback<PosHeader, CurrencyExchnageFilter> countCallback, ValueProvider<PosHeader, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
