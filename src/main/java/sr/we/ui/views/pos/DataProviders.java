package sr.we.ui.views.pos;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import sr.we.ContextProvider;
import sr.we.data.controller.ProductService;
import sr.we.data.controller.ServicesService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataProviders extends Div {



    static CallbackDataProvider<ProductOrService, String> getServices(String business) {
        CallbackDataProvider<ProductOrService, String> dataProvider;
        dataProvider = DataProvider.fromFilteringCallbacks(query -> {
            int pageSize = query.getPageSize();
            int page = query.getPage();

            List<ProductOrService> list = new ArrayList<>();
            ServicesService productService = ContextProvider.getBean(ServicesService.class);
            List<Services> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());
            return list.stream();
        }, query -> {
            ServicesService productService = ContextProvider.getBean(ServicesService.class);
            List<Services> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            return list1.size();
        });
        return dataProvider;
    }

    static DataProvider<ProductOrServiceGrid, Void> getProductsGrid(String business) {
        DataProvider<ProductOrServiceGrid, Void> dataProvider;
        dataProvider = DataProvider.fromCallbacks(query -> {
            int pageSize = query.getPageSize();
            int page = query.getPage();

            List<ProductOrService> list = new ArrayList<>();
            ProductService productService = ContextProvider.getBean(ProductService.class);
            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());
            List<ProductOrServiceGrid> productOrServiceGrids = toGrid(list);
            return productOrServiceGrids.stream();
        }, query -> {
            ProductService productService = ContextProvider.getBean(ProductService.class);
            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            return rowSize(list1);
        });
        return dataProvider;
    }

    static CallbackDataProvider<ProductOrService, String> getProducts(String business) {
        CallbackDataProvider<ProductOrService, String> dataProvider;
        dataProvider = DataProvider.fromFilteringCallbacks(query -> {
            int pageSize = query.getPageSize();
            int page = query.getPage();

            List<ProductOrService> list = new ArrayList<>();
            ProductService productService = ContextProvider.getBean(ProductService.class);
            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());
            return list.stream();
        }, query -> {
            ProductService productService = ContextProvider.getBean(ProductService.class);
            List<Product> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            return list1.size();
        });
        return dataProvider;
    }

    static DataProvider<ProductOrServiceGrid, Void> getServicesGrid(String business) {
        DataProvider<ProductOrServiceGrid, Void> dataProvider;
        dataProvider = DataProvider.fromCallbacks(query -> {
            int pageSize = query.getPageSize();
            int page = query.getPage();

            List<ProductOrService> list = new ArrayList<>();
            ServicesService productService = ContextProvider.getBean(ServicesService.class);
            List<Services> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            list = list1.stream().map(ProductOrService::new).collect(Collectors.toList());

            List<ProductOrServiceGrid> list2 = toGrid(list);
            return list2.stream();
        }, query -> {
            ServicesService productService = ContextProvider.getBean(ServicesService.class);
            List<Services> list1 = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
            return rowSize(list1);
        });
        return dataProvider;
    }

    private static List<ProductOrServiceGrid> toGrid(List<ProductOrService> list) {
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
