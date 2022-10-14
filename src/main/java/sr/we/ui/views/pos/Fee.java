package sr.we.ui.views.pos;

import sr.we.ContextProvider;
import sr.we.data.controller.CalculationService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.CalculationComponent;
import sr.we.shekelflowcore.entity.PosHeaderDetail;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationParam;
import sr.we.shekelflowcore.entity.helper.adapter.CalculationResult;
import sr.we.shekelflowcore.settings.util.Rubric;

import java.math.BigDecimal;

public class Fee {
//    private final IFee posView;
//    private final CalculationComponent calculationComponent;
//    private String title;
//    private BigDecimal price;
//    private CalculationResult calculate;
//    private PosHeaderDetail posHeaderDetail;
//
//    public Fee(IFee posView, PosHeaderDetail posHeaderDetail,  CalculationComponent calculationComponent) {
//        this(posView,calculationComponent);
//        this.posHeaderDetail = posHeaderDetail;
//        this.price = posHeaderDetail.getPrice();
//    }
//
//    public Fee(IFee posView, CalculationComponent calculationComponent) {
//        this.posView = posView;
//        this.calculationComponent = calculationComponent;
//        this.title = calculationComponent.getName();
//        this.price = BigDecimal.ZERO;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public CalculationComponent getCalculationComponent() {
//        return calculationComponent;
//    }
//
//    public CalculationResult getCalculate() {
//        return calculate;
//    }
//
//    public PosHeaderDetail getPosHeaderDetail() {
//        return posHeaderDetail;
//    }
//
//    public BigDecimal getCalcPrice() {
//        price = price();
//        return price;
//    }
//
//    private BigDecimal price() {
//        CalculationService calculationService = ContextProvider.getBean(CalculationService.class);
//        CalculationParam vo = new CalculationParam();
//        vo.setCalculationComponent(calculationComponent.getId());
//        BigDecimal reduce = posView.getItemList().stream().map(Item::getResult).reduce(BigDecimal.ZERO, BigDecimal::add);
//        posView.addFeeMap(Rubric.SUB_TOTAL.getTitle(), reduce.toString());
//        vo.setMap(posView.getFeeMap());
//        calculate = calculationService.calculate(AuthenticatedUser.token(), vo);
//        return calculate.getResult() == null ? BigDecimal.ZERO : calculate.getResult();
//    }
}
