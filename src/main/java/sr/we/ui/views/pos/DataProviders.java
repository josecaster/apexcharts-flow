package sr.we.ui.views.pos;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.ItemsService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.helper.vo.PageRequestImpl;
import sr.we.shekelflowcore.entity.helper.vo.ServicesVO;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;
import sr.we.shekelflowcore.entity.helper.vo.SuperVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataProviders extends Div {


    public static CallbackDataProvider<ProductOrService, String> getServices(String business) {
        CallbackDataProvider<ProductOrService, String> dataProvider;
        dataProvider = DataProvider.fromFilteringCallbacks(query -> {
            ServicesVO vo = new ServicesVO();
            vo.setBusiness(Long.valueOf(business));
            filterString(query, vo);
            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            List<Items> list1 = productService.list(AuthenticatedUser.token(), vo).getResult();
            List<ProductOrService> list = list1.stream().map(ProductOrService::new).toList();
            return list.stream();
        }, query -> {
            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            ServicesVO vo = new ServicesVO();
            vo.setBusiness(Long.valueOf(business));
            filterStringCount(query, vo);
            return productService.list(AuthenticatedUser.token(), vo).getCount().intValue();
        });
        return dataProvider;
    }

    public static DataProvider<Items, Void> getItems(ServicesVO vo) {
        DataProvider<Items, Void> dataProvider;
        dataProvider = DataProvider.fromFilteringCallbacks(getItemsVoidFetchCallback(vo), getItemsVoidCountCallback(vo));
        return dataProvider;
    }

    public static CallbackDataProvider.CountCallback<Items, Void> getItemsVoidCountCallback(ServicesVO vo) {
        return query -> {
            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            filterVoidCount(vo);
            return productService.list(StringUtils.isBlank(vo.getToken())? AuthenticatedUser.token() : vo.getToken(), vo).getCount().intValue();
        };
    }

    public static CallbackDataProvider.FetchCallback<Items, Void> getItemsVoidFetchCallback(ServicesVO vo) {
        return query -> {
            filterVoid(query, vo);
            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            List<Items> list1 = productService.list(StringUtils.isBlank(vo.getToken())? AuthenticatedUser.token() : vo.getToken(), vo).getResult();
            return list1.stream();
        };
    }

    private static void filterStringCount(Query<?, String> query, SuperVO vo) {
        PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
        if(query.getFilter().isPresent()) {
            pageRequest.setFilter(query.getFilter().get());
        }
        vo.setPageRequest(pageRequest);
    }

    private static void filterString(Query<?, String> query, SuperVO vo) {
        PageRequestImpl pageRequest = PageRequestImpl.of(query.getPage(), query.getPageSize());
        if(query.getFilter().isPresent()) {
            pageRequest.setFilter(query.getFilter().get());
        }
        vo.setPageRequest(pageRequest);
    }

    private static void filterVoidCount(SuperVO vo) {
        PageRequestImpl pageRequest = PageRequestImpl.of(0, 1);
        vo.setPageRequest(pageRequest);
    }

    private static void filterVoid(Query<?, Void> fetch, SuperVO vo) {
        PageRequestImpl pageRequest = vo.getSort() == null ? PageRequestImpl.of(fetch.getPage(), fetch.getPageSize()) : PageRequestImpl.of(fetch.getPage(), fetch.getPageSize(), SortImpl.by(vo.getSort()));
        vo.setPageRequest(pageRequest);
    }

//    static DataProvider<ProductOrServiceGrid, Void> getProductsGrid(String business) {
//        DataProvider<ProductOrServiceGrid, Void> dataProvider;
//        dataProvider = DataProvider.fromCallbacks(query -> {
//            int pageSize = query.getPageSize();
//            int page = query.getPage();
//
//            List<ProductOrService> list = new ArrayList<>();
//            ProductService productService = ContextProvider.getBean(ProductService.class);
////            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
////            list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());
//            List<ProductOrServiceGrid> productOrServiceGrids = toGrid(list);
//            return productOrServiceGrids.stream();
//        }, query -> {
//            ProductService productService = ContextProvider.getBean(ProductService.class);
//            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
//            return rowSize(list1);
//        });
//        return dataProvider;
//    }

//    static CallbackDataProvider<ProductOrService, String> getProducts(String business) {
//        CallbackDataProvider<ProductOrService, String> dataProvider;
//        dataProvider = DataProvider.fromFilteringCallbacks(query -> {
//            int pageSize = query.getPageSize();
//            int page = query.getPage();
//
//            List<ProductOrService> list = new ArrayList<>();
//            ProductService productService = ContextProvider.getBean(ProductService.class);
////            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
////            list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());
//            return list.stream();
//        }, query -> {
//            ProductService productService = ContextProvider.getBean(ProductService.class);
//            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
//            return list1.size();
//        });
//        return dataProvider;
//    }

    public static DataProvider<ProductOrServiceGrid, Void> getServicesGrid(String business) {
        DataProvider<ProductOrServiceGrid, Void> dataProvider;
        dataProvider = DataProvider.fromCallbacks(query -> {
            ServicesVO vo = new ServicesVO();
            vo.setBusiness(Long.valueOf(business));
            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            List<Items> list1 = productService.list(AuthenticatedUser.token(), vo).getResult();
            List<ProductOrService> list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());

            List<ProductOrServiceGrid> list2 = toGrid(list);
            return list2.stream();
        }, query -> {
            ItemsService productService = ContextProvider.getBean(ItemsService.class);
            ServicesVO vo = new ServicesVO();
            vo.setBusiness(Long.valueOf(business));
            List<Items> list1 = productService.list(AuthenticatedUser.token(), vo).getResult();
            return rowSize(list1);
        });
        return dataProvider;
    }

    public static List<ProductOrServiceGrid> toGrid(List<ProductOrService> list) {
        List<ProductOrServiceGrid> list2 = new ArrayList<>();
        ProductOrServiceGrid productOrServiceGrid = null;
        for (int i = 0; i < list.size(); i++) {
            int i1 = i % 4;
            if (i1 == 0) {
                productOrServiceGrid = new ProductOrServiceGrid();
                productOrServiceGrid.setOne(list.get(i));
                list2.add(productOrServiceGrid);
            } else if (i1 == 1) {
                productOrServiceGrid.setTwo(list.get(i));
            } else if (i1 == 2) {
                productOrServiceGrid.setThree(list.get(i));
            } else {
                productOrServiceGrid.setFour(list.get(i));
            }
        }
        return list2;
    }


    private static int rowSize(List<?> list1) {
        int i = list1.size() % 4;
        return (list1.size() / 4) + (i == 0 ? 0 : 1);
    }
}
