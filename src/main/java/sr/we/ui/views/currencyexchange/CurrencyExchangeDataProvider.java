package sr.we.ui.views.currencyexchange;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import org.springframework.data.domain.PageRequest;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.CurrencyExchange;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.CurrencyExchangeVO;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;

public class CurrencyExchangeDataProvider {

    public static CallbackDataProvider.CountCallback<CurrencyExchange, Void> count(CurrencyExchangeVO vo) {
        return count -> {
            ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
            PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
            vo.setPageRequest(pageRequest);
            PagingResult<CurrencyExchange> list = exchangeRateService.list(vo.getToken(), vo);
            return list.getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<CurrencyExchange, Void> fetch(CurrencyExchangeVO vo) {
        return fetch -> {
            ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
            PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
            vo.setPageRequest(pageRequest);
            PagingResult<CurrencyExchange> list = exchangeRateService.list(vo.getToken(), vo);
            return list.getResult().stream();
        };
    }

    /*public CurrencyExchangeDataProvider(FetchCallback<CurrencyExchange, CurrencyExchnageFilter> fetchCallBack, CountCallback<CurrencyExchange, CurrencyExchnageFilter> countCallback, ValueProvider<CurrencyExchange, Object> identifierGetter) {
        super(fetchCallBack, countCallback, identifierGetter);
    }*/
}
