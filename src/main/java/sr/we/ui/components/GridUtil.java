package sr.we.ui.components;

import com.flowingcode.vaadin.addons.gridexporter.GridExporter;
import com.flowingcode.vaadin.addons.gridhelpers.GridHelper;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.event.SortEvent;
import com.vaadin.flow.data.provider.SortDirection;
import sr.we.shekelflowcore.entity.helper.vo.SortImpl;
import sr.we.shekelflowcore.entity.helper.vo.SuperVO;
import sr.we.ui.views.LineAwesomeIcon;

import java.util.List;

public class GridUtil {

    public static <T> void onComponentEvent(SortEvent<Grid<T>, GridSortOrder<T>> f, SuperVO filter) {
        List<GridSortOrder<T>> sortOrder = f.getSortOrder();
        if(!sortOrder.isEmpty()) {
            sortOrder.forEach(g -> {
                Grid.Column<T> sorted = g.getSorted();
                SortDirection direction = g.getDirection();
                SortImpl.Order sort = null;
                if (sorted.getId().isPresent()) {
                    switch (direction) {
                        case ASCENDING -> {
                            sort = SortImpl.Order.asc(sorted.getId().get());
                        }
                        case DESCENDING -> {
                            sort = SortImpl.Order.desc(sorted.getId().get());
                        }
                    }
                }
                filter.setSort(sort);
            });
        } else {
            filter.setSort(null);
        }
    }

    public static void exportButtons(GridExporter exporter, Grid grid){
        exportButtons(exporter,grid,null,null);
    }

    public static void exportButtons(GridExporter exporter, Grid grid, String excelCustomTemplate, String docxCustomTemplate){
        exporter.setAutoAttachExportButtons(false);
        HorizontalLayout hl = new HorizontalLayout();
        if (exporter.isExcelExportEnabled()) {
            Anchor excelLink = new Anchor("", new MyIcon("icons/icons8_xls_48px.png", "icon by Icons8"));
            excelLink.setHref(exporter.getExcelStreamResource(excelCustomTemplate));
            excelLink.getElement().setAttribute("download", true);
            hl.add(excelLink);
        }
        if (exporter.isDocxExportEnabled()) {
            Anchor docLink = new Anchor("", new MyIcon("icons/icons8_word_48px.png", "icon by Icons8"));
            docLink.setHref(exporter.getDocxStreamResource(docxCustomTemplate));
            docLink.getElement().setAttribute("download", true);
            hl.add(docLink);
        }
        if (exporter.isPdfExportEnabled()) {
            Anchor docLink = new Anchor("", new MyIcon("icons/icons8_pdf_48px.png", "icon by Icons8"));
            docLink.setHref(exporter.getPdfStreamResource(docxCustomTemplate));
            docLink.getElement().setAttribute("download", true);
            hl.add(docLink);
        }
        if (exporter.isCsvExportEnabled()) {
            Anchor csvLink = new Anchor("", new MyIcon("icons/icons8_csv_48px.png", "icon by Icons8"));
            csvLink.setHref(exporter.getCsvStreamResource());
            csvLink.getElement().setAttribute("download", true);
            hl.add(csvLink);
        }
        hl.setSizeFull();

        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        GridHelper.addToolbarFooter(grid, hl);
    }
}
