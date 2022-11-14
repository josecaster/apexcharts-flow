package sr.we.ui.views.finance.loans.tabs.request;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestAssetsPrivilege;
import sr.we.shekelflowcore.security.privileges.LoanRequestPlanPrivilege;
import sr.we.shekelflowcore.security.privileges.LoanRequestPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.FieldSet;
import sr.we.ui.components.Highlight;
import sr.we.ui.components.MyDialog;
import sr.we.ui.components.finance.LoanRequestStatusWizard;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.finance.loans.LoansView;
import sr.we.ui.views.finance.loans.tabs.request.assets.InputLayout;
import sr.we.ui.views.finance.loans.tabs.request.assets.LRAssetsLayout;
import sr.we.ui.views.finance.loans.tabs.request.planning.LRPGenerate;
import sr.we.ui.views.finance.loans.tabs.request.planning.LRPView;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static sr.we.ContextProvider.getBean;

@Route(value = "requests-overview", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LRView extends VerticalLayout implements BeforeEnterObserver {

    private final H2 header;
    private final Span currency;
    private final Div details;
    private final VerticalLayout layout;
    private final LoanRequestStatusWizard loanRequestStatusWizard;
    private final Tabs tabs;

    private String business;

    private LoanRequest loanRequest;

    public LRView() {
        header = new H2();


//        add(header);
        currency = new Span();
        currency.addClassNames("text-secondary", "text-xs");
//        add(currency);

        loanRequestStatusWizard = new LoanRequestStatusWizard();
        add(loanRequestStatusWizard);

        details = new Div();
//        details.addThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE);
        loanRequestStatusWizard.setContent(details);

        Tab payments = new Tab("Payments");

        tabs = new Tabs(payments);
        add(tabs);

        layout = new VerticalLayout();
        layout.setSizeFull();

        add(layout);

        tabs.addSelectedChangeListener(f -> {
            layout.removeAll();
            if (f.getSelectedTab().equals(payments)) {
                layout.add(new LRPGenerate(null, loanRequest, null));
            }
        });

        setPadding(false);
        setMargin(false);

    }

    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/requests-overview";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        RouteParameters routeParameters = event.getRouteParameters();
        Optional<String> business1 = routeParameters.get("business");
        if (business1.isPresent()) {
            business = business1.get();
        } else {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        QueryParameters queryParams = event.getLocation().getQueryParameters();
        List<String> id1 = queryParams.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        String token = AuthenticatedUser.token();
        LoanRequestService loanRequestService = getBean(LoanRequestService.class);
        loanRequest = loanRequestService.get(Long.valueOf(id.get()), token);
        if (loanRequest == null) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        Customer customer = loanRequest.getCustomer();
        BigDecimal amount = loanRequest.getAmount();
        LoanRequest.Status status = loanRequest.getStatus();

        header.setText(customer.getName() + (StringUtils.isBlank(customer.getFirstName()) ? "" : " " + customer.getFirstName()));
        currency.setText(amount == null ? "" : amount.toPlainString());
        dodo(loanRequestService, status, false);

        loanRequestStatusWizard.addTabListener(new Executable() {
            @Override
            public Object build() {
                LoanRequestService loanRequestService = getBean(LoanRequestService.class);
                dodo(loanRequestService, loanRequestStatusWizard.getStatus(), true);
                loanRequestStatusWizard.getStatus();
                return null;
            }
        });


        boolean visible = status.compareTo(LoanRequest.Status.APPROVED) != 0;

        tabs.setVisible(visible);
        if (!visible) {
            layout.removeAll();
        }

    }

    private void dodo(LoanRequestService loanRequestService, LoanRequest.Status status, boolean disable) {
        Customer customer = loanRequest.getCustomer();
        Currency currency1 = loanRequest.getCurrency();
        BigDecimal amount = loanRequest.getAmount();
        LoanRequest.Status status1 = loanRequest.getStatus();

        H4 summary = new H4(customer.getName() + " " + currency1.getCode() + " " + (amount == null ? "" : amount.toPlainString()));
        summary.getElement().getStyle().set("margin-block-start", "0px").set("margin-block-end", "0px");
        HorizontalLayout card = new HorizontalLayout(summary);
        switch (status1) {
            case REQUESTED -> {
                Span pending = new Span("Requested");
                pending.getElement().getThemeList().add("badge");
                card.add(pending);
                break;
            }
            /*case ELIGIBLE -> {
                Span approved = new Span("Eligible");
                approved.getElement().getThemeList().add("badge success");
                card.add(approved);
                break;
            }
            case REVIEW -> {
                Span review = new Span("Review");
                review.getElement().getThemeList().add("badge contrast");
                card.add(review);
                break;
            }
            case INPUT -> {
                Span input = new Span("Waiting on Input");
                input.getElement().getThemeList().add("badge error");
                card.add(input);
                break;
            }*/
        }

        loanRequestStatusWizard.setLoanRequestStatus(status1, loanRequest.getId());
        details.removeAll();

        switch (status) {
           /* case CLOSED -> {
                VerticalLayout layout1 = new VerticalLayout();
                layout1.setSizeFull();
                Image img = new Image("images/empty-plant.png", "placeholder plant");
                img.setWidth("200px");
                img.setHeight("200px");
                layout1.add(img);

                layout1.add(new H2("Closed"));
                layout1.add(new Paragraph("some text here"));

                layout1.setSizeFull();
                layout1.setJustifyContentMode(JustifyContentMode.CENTER);
                layout1.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
                layout1.getStyle().set("text-align", "center");
                details.add(layout1);
                break;
            }*/
            case APPROVED -> {
                VerticalLayout layout1 = new VerticalLayout();
                layout1.setSizeFull();
                Image img = new Image("images/empty-plant.png", "placeholder plant");
                img.setWidth("200px");
                img.setHeight("200px");
                layout1.add(img);

                layout1.add(new H2("Approved, sign the contracts...."));
                layout1.add(new Paragraph("Here you will have the ability to upload the contracts"));

                layout1.setSizeFull();
                layout1.setJustifyContentMode(JustifyContentMode.CENTER);
                layout1.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
                layout1.getStyle().set("text-align", "center");
                details.add(layout1);
                break;
            }
            case REQUESTED -> {
                requested(card);
                payments(loanRequestService, layout);
                break;
            }
           /* case ELIGIBLE -> {
                payments(loanRequestService, layout);
                VerticalLayout layout1 = new VerticalLayout();
                layout1.setSizeFull();
                Image img = new Image("images/empty-plant.png", "placeholder plant");
                img.setWidth("200px");
                img.setHeight("200px");
                layout1.add(img);

                layout1.add(new H2("The request has been marked as Eligible"));
                layout1.add(new Paragraph("The authorized person can now approve the loan"));

                layout1.setSizeFull();
                layout1.setJustifyContentMode(JustifyContentMode.CENTER);
                layout1.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
                layout1.getStyle().set("text-align", "center");
                details.add(layout1);
                break;
            }
            case REVIEW -> {
                review(card);
                payments(loanRequestService, layout);
                break;
            }
            case INPUT -> {
                input(card);
                payments(loanRequestService, layout);
                break;
            }
            case PROVIDE -> {
                ProvideLayout provideLayout = new ProvideLayout();
                provideLayout.setLoanRequest(loanRequest, null);
                details.add(provideLayout);
                payments(loanRequestService, layout);
                break;
            }
            case REPAYMENT -> {
//                LRPView loanRequestsPlanningView = new LRPView(loanRequestPlan, loanRequest, new Build() {
//                    @Override
//                    public Object build() {
//                        UI.getCurrent().getPage().reload();
//                        return null;
//                    }
//                });
//                layout.add(loanRequestsPlanningView);

                payments(loanRequestService, details);
                break;
            }*/
        }
    }

    private void payments(LoanRequestService loanRequestService, HasComponents layout) {
        List<LoanRequestPlan> loanRequestPlans = loanRequestService.listPlan(AuthenticatedUser.token(), loanRequest.getId()).getResult();
        layout.removeAll();
        BsLayout board = new BsLayout();
        layout.add(board);

        Long reduce = 0l;
        if (loanRequestPlans != null && !loanRequestPlans.isEmpty()) {
            reduce = loanRequestPlans.stream().map(f -> f.getFreqAmount()).reduce(0l, (subtotal, element) -> subtotal + element);
        }

        Highlight principal = new Highlight("Principal", () -> Constants.CURRENCY_FORMAT.format(loanRequest.getAmount()), () -> null);
        Long finalReduce = reduce;
        Highlight initial_frequency = new Highlight("Initial Frequency", () -> loanRequest.getFreq().getCaption() + " " + loanRequest.getFreqVal().doubleValue(), () -> finalReduce.doubleValue());
        Highlight intrest = new Highlight("Intrest", () -> loanRequest.getIntrest() == null ? Constants.CURRENCY_FORMAT.format(0) : Constants.CURRENCY_FORMAT.format(loanRequest.getIntrest()), () -> null);
        Highlight balance = new Highlight("Balance", () -> loanRequest.getBalance() == null ? Constants.CURRENCY_FORMAT.format(0) : Constants.CURRENCY_FORMAT.format(loanRequest.getBalance()), () -> loanRequest.getTransactionBalance() == null ? null : loanRequest.getTransactionBalance().doubleValue());
        board.withRows(new BsRow().withColumns(new BsColumn(principal).withSize(BsColumn.Size.XS), new BsColumn(initial_frequency).withSize(BsColumn.Size.XS), new BsColumn(intrest).withSize(BsColumn.Size.XS), new BsColumn(balance).withSize(BsColumn.Size.XS)));


        LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-plus");
        layout.add(lineAwesomeIcon);
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPlanPrivilege(), Privileges.INSERT, Privileges.EXECUTE);
        lineAwesomeIcon.setVisible(hasAccess);
        Dialog dialog = new MyDialog();
        dialog.setHeaderTitle("Generate payment plan");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            dialog.close();
            UI.getCurrent().getPage().executeJs("return window.location.href").then(String.class, location -> {
                UI.getCurrent().getPage().setLocation(location);
            });

        });
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(true);
        dialog.setResizable(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        lineAwesomeIcon.addClickListener(f -> {

            LRPGenerate loanRequestsPlanningView = new LRPGenerate(null, loanRequest, new Executable() {
                @Override
                public Object build() {
                    dialog.close();
                    UI.getCurrent().getPage().reload();
                    return null;
                }
            });
            dialog.removeAll();
            dialog.add(loanRequestsPlanningView);
            dialog.open();
        });
//        } else {
//            layout.removeAll();
        Accordion accordion = new Accordion();
        accordion.setSizeFull();
        layout.add(accordion);
        loanRequestPlans = loanRequestPlans.stream().sorted(Comparator.comparingLong(LoanRequestPlan::getId)).collect(Collectors.toList());
        for (LoanRequestPlan loanRequestPlan : loanRequestPlans) {

            LRPView loanRequestsPlanningView = new LRPView(loanRequestPlan, loanRequest, new Executable() {
                @Override
                public Object build() {
                    UI.getCurrent().getPage().reload();
                    return null;
                }
            });
            AccordionPanel add = accordion.add(loanRequestPlan.getType().toString() + " " + Constants.CURRENCY_FORMAT.format(loanRequestPlan.getAmount()), loanRequestsPlanningView);
            add.addThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE);
        }
//        }
    }

    private void input(HorizontalLayout card) {
        details.removeAll();
        LRCreateLayout loanRequestViewNewLayout = new LRCreateLayout();
        loanRequestViewNewLayout.setLoanRequest(loanRequest);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1)/*, new FormLayout.ResponsiveStep("500", 2)*/);

        Div div1 = new Div(card, loanRequestViewNewLayout);
        VerticalLayout assets = new VerticalLayout();


        LRAssetsLayout lrAssetsLayout = new LRAssetsLayout(loanRequest);
        Dialog dialog = new Dialog(lrAssetsLayout);
        lrAssetsLayout.setBuild(new Executable() {
            @Override
            public Object build() {
                dialog.close();
                return null;
            }
        });
        dialog.setHeaderTitle("Add assets");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            dialog.close();
//            UI.getCurrent().getPage().reload();
        });
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);

        Button addAssets = new Button("Add assets");
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestAssetsPrivilege(), Privileges.INSERT);
        addAssets.setVisible(hasAccess);
        addAssets.addClickListener(f -> {
            dialog.open();
        });
        String q = "Total asset value: " + loanRequest.getCurrency().getCode();
        if (loanRequest.getLoanRequestAssets() != null && !loanRequest.getLoanRequestAssets().isEmpty()) {
            BigDecimal reduce = loanRequest.getLoanRequestAssets().stream().filter(f -> f.getValid()).map(f -> f.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            q += " " + reduce.toPlainString();
        } else {
            q += " 0.00";
        }
        assets.add(new HorizontalLayout(addAssets, new H4(q)));
        assets.setWidthFull();
//        LoanRequestAssetsFilesService loanRequestAssetsFilesService = ContextProvider.getBean(LoanRequestAssetsFilesService.class);
//        List<LoanRequestAssetsFiles> list = loanRequestAssetsFilesService.list(AuthenticatedUser.token(), null, loanRequest.getId());
//        if(list != null && !list.isEmpty()){
//            Map<LoanRequestAssets, List<LoanRequestAssetsFiles>> collect =
//                    list.stream().collect(Collectors.groupingBy(LoanRequestAssetsFiles::getLoanRequestAssets));
//            for(Map.Entry<LoanRequestAssets, List<LoanRequestAssetsFiles>> entry : collect.entrySet()){
//                FieldSet fieldSet = new FieldSet(entry.getKey().getLoanAssets() + "");
//                assets.add(fieldSet);
//                InputLayout<LoanRequestAssetsFiles> inputLayout = new InputLayout<LoanRequestAssetsFiles>();
//                inputLayout.getFileGrid().setItems(entry.getValue());
//                fieldSet.getContent().add(inputLayout);
//            }
//        }
        FormLayout formLayout1 = new FormLayout();
        formLayout1.setWidthFull();
        assets.setWidthFull();
        assets.add(formLayout1);
        formLayout1.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500", 2));
        if (loanRequest.getLoanRequestAssets() != null && !loanRequest.getLoanRequestAssets().isEmpty()) {
            for (LoanRequestAssets loanRequestAssets : loanRequest.getLoanRequestAssets()) {
                FieldSet fieldSet = new FieldSet(loanRequestAssets.getLoanAssets().getName());
                fieldSet.getElement().getStyle().set("max-width", "500px");
                formLayout1.add(new VerticalLayout(fieldSet));
                InputLayout inputLayout = new InputLayout();
                inputLayout.getFileGrid().setItems(loanRequestAssets.getLoanRequestAssetsFiles());
                inputLayout.setLoanRequestAssets(loanRequestAssets);
                fieldSet.getContent().add(inputLayout);
            }
        }
//        loanRequestAssetsGrid.getDataProvider().fetch(new HierarchicalQuery<>(null, null)).collect(Collectors.toList()).addAll(list);
//        loanRequestAssetsGrid.getDataProvider().refreshAll();
////        loanRequestAssetsGrid.setItems(list.get(0), list);
//        loanRequestAssetsGrid.addHierarchyColumn(f -> f.getLoanAssets().getName());
//        loanRequestAssetsGrid.addColumn(new ValueProvider<LoanRequestAssetsFiles, String>() {
//            @Override
//            public String apply(LoanRequestAssetsFiles loanRequestAssets) {
//                return loanRequestAssets.getLoanAssets().getName();
//            }
//        }).setHeader("Type");
//
//        loanRequestAssetsGrid.addComponentColumn(new ValueProvider<LoanRequestAssetsFiles, Checkbox>() {
//            @Override
//            public Checkbox apply(LoanRequestAssetsFiles loanRequestAssets) {
//                return new Checkbox(loanRequestAssets.getValid());
//            }
//        }).setHeader("Valid");
//        loanRequestAssetsGrid.addComponentColumn(new ValueProvider<LoanRequestAssetsFiles, BigDecimalField>() {
//            @Override
//            public BigDecimalField apply(LoanRequestAssetsFiles loanRequestAssets) {
//                BigDecimalField bigDecimalField = new BigDecimalField();
//                bigDecimalField.setValue(loanRequestAssets.getLoanRequestAssets().getAmount());
//                return bigDecimalField;
//            }
//        }).setHeader("Valued at");
//        loanRequestAssetsGrid.addColumn(new ValueProvider<LoanRequestAssetsFiles, String>() {
//            @Override
//            public String apply(LoanRequestAssetsFiles loanRequestAssets) {
//                return loanRequestAssets.getLoanRequestAssets().getMemo();
//            }
//        }).setHeader("Memo");
//        loanRequestAssetsGrid.setAllRowsVisible(true);

//        FieldSet form = new FieldSet("Forms");
//        form.getContent().add("Not yet implemented");
//        form.setWidthFull();

        VerticalLayout div = new VerticalLayout(assets);
        div.setWidthFull();
        div.setMargin(false);
        div.setPadding(false);
//        VerticalLayout div2 = new VerticalLayout(form);
//        div2.setWidthFull();
//        div2.setMargin(false);
//        div2.setPadding(false);
        formLayout.add(div/*, div2*/);
        details.add(formLayout);
        details.setWidthFull();
    }

    private void review(HorizontalLayout card) {
        details.removeAll();
        LRCreateLayout loanRequestViewNewLayout = new LRCreateLayout();
        loanRequestViewNewLayout.setLoanRequest(loanRequest);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500", 2));


        VerticalLayout div = new VerticalLayout();
//                Button title = new Button("Invite customer to login");
//                title.addClickListener(f -> {
//                    throw new ValidationException("Not implemented yet");
//                });
//
//                div.add(title);
        div.setSizeFull();
        div.setJustifyContentMode(JustifyContentMode.CENTER);
        div.setAlignItems(Alignment.CENTER);

        Avatar avatar = new Avatar(loanRequest.getName()/*, user.getProfilePictureUrl()*/);
        avatar.addClassNames("me-xl");
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        div.add(new H2("Debtor's profile"));
        div.add(avatar);

//        div.add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
        BsLayout board = new BsLayout();
        board.withRows(new BsRow().withColumns(new BsColumn(new Highlight("Open loan requests", () -> "3", () -> 80.0)), //
                new BsColumn(new Highlight("Closed loans", () -> "2", () -> 100.0)), //
                new BsColumn(new Highlight("Approved loans", () -> "3", () -> 80.0)), //
                new BsColumn(new Highlight("Declined loans", () -> "0", () -> 0.0))));
        div.add(board);


        Div div1 = new Div(card, loanRequestViewNewLayout);
        formLayout.add(div, div1);
        details.add(formLayout);
    }

    private void requested(HorizontalLayout card) {
        details.removeAll();
        details.add(card);
        LRCreateLayout loanRequestViewNewLayout = new LRCreateLayout();
        loanRequestViewNewLayout.setLoanRequest(loanRequest);
        details.add(loanRequestViewNewLayout);
    }
}
