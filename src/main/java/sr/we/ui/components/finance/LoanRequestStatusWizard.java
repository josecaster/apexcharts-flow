package sr.we.ui.components.finance;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.ui.views.LineAwesomeIcon;

public class LoanRequestStatusWizard extends VerticalLayout {


    private final Tab requested, review, input, eligible, approved, provide, repayment, closed;
    private final Button back;
    private final Button next;
    private final Div content;
    private final Tabs tabs;

    private Executable nextExecutable, backExecutable;

    public LoanRequestStatusWizard() {

        getElement().getStyle().set("background-image", "linear-gradient(0deg, var(--lumo-shade-5pct), var(--lumo-shade-5pct))");
        getElement().getStyle().set("border-radius", "calc(var(--lumo-size-m) / 4)");

        back = new Button("Back");
        next = new Button("Next");
        next.addClickListener(e -> {
            if (nextExecutable != null) {
                nextExecutable.build();
            }
        });
        back.addClickListener(e -> {
            if (backExecutable != null) {
                backExecutable.build();
            }
        });
        HorizontalLayout header = new HorizontalLayout(back, next);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.END);
        header.setAlignItems(Alignment.END);
        back.getElement().getStyle().set("margin-right", "auto");

//        getTab().getClassList().add("wizard");
        tabs = new Tabs();

        tabs.setWidthFull();

        requested = new Tab(new Span("Requested"));
        review = new Tab(new Span("Review"));
        input = new Tab(new Span("Input"));
        eligible = new Tab(new Span("Eligible"));
        approved = new Tab(new Span("Approved"));
        provide = new Tab(new Span("Provide"));
        repayment = new Tab(new Span("Repayment"));
        closed = new Tab(new Span("Closed"));
//

        requested.setEnabled(false);
        review.setEnabled(false);
        input.setEnabled(false);
        eligible.setEnabled(false);
        approved.setEnabled(false);
        provide.setEnabled(false);
        repayment.setEnabled(false);
        closed.setEnabled(false);
//        getTab().appendChild(requested, review, input, approved);

        Tab tab = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab.setEnabled(false);
        Tab tab1 = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab1.setEnabled(false);
        Tab tab2 = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab2.setEnabled(false);
        Tab tab3 = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab3.setEnabled(false);
        Tab tab4 = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab4.setEnabled(false);
        Tab tab5 = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab5.setEnabled(false);
        Tab tab6 = new Tab(new LineAwesomeIcon("la la-arrow-right"));
        tab6.setEnabled(false);
        tabs.add(requested, tab, review, tab1, input, tab2, eligible, tab3, approved, tab4, provide, tab5, repayment, tab6, closed);

        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED);

        add(tabs);
        content = new Div();
        add(content);
        add(header);
        content.setWidthFull();
    }

    public void setContent(Component component) {
        content.removeAll();
        content.add(component);
    }

    public void setLoanRequestStatus(LoanRequest.Status status, Long loanRequestId) {
        /*switch (status) {
            case REQUESTED -> {
                select(status);

                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Review Loan", "You are about to continue this proces to the review phase, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.REVIEW);
                            updateNextStatus(loanRequestId, LoanRequest.Status.REVIEW);
                        });
                        return null;
                    }
                };
                backBuild = null;
                break;
            }
            case REVIEW -> {
                select(status);
                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Input Loan", "You are about to continue this proces to the input phase in which you will request additional user info, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.INPUT);
                            updateNextStatus(loanRequestId, LoanRequest.Status.INPUT);
                        });
                        return null;
                    }
                };
                backBuild = null;
                break;
            }
            case INPUT -> {
                select(status);

                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Approve Loan", "You are about to continue this proces to the approval phase on which this loan request will be marked as eligible, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.APPROVED);
                            updateNextStatus(loanRequestId, LoanRequest.Status.ELIGIBLE);
                        });
                        return null;
                    }
                };
                backBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Input Loan", "You are about to go back to the input phase in which you will request additional user info, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.REVIEW);
                            updatePrevStatus(loanRequestId, LoanRequest.Status.REVIEW);
                        });
                        return null;
                    }
                };
                break;
            }
            case ELIGIBLE -> {
                select(status);


                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Eligible Loan", "You are about to continue this proces to the approval phase on which this loan request will be marked as eligible, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.APPROVED);
                            updateNextStatus(loanRequestId, LoanRequest.Status.APPROVED);
                        });
                        return null;
                    }
                };
                backBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Cancel Loan", "You are about to cancel this loan request, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            Notification.show("Canceled loan request");
                            updateNextStatus(loanRequestId, status);
                        });
                        return null;
                    }
                };
                break;
            }
            case APPROVED -> {
                select(status);


                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Approve Loan", "You are about to continue this proces to the approval approval on which this loan request will be marked as approved, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.APPROVED);
                            updateNextStatus(loanRequestId, LoanRequest.Status.PROVIDE);
                        });
                        return null;
                    }
                };
                backBuild = null;
                break;
            }
            case PROVIDE -> {
                select(status);


                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Provide Loan", "You are about to continue this proces to the providing phase on which this loan request will be marked as providing, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.APPROVED);
                            updateNextStatus(loanRequestId, LoanRequest.Status.REPAYMENT);
                        });
                        return null;
                    }
                };
                backBuild = null;
                break;
            }
            case REPAYMENT -> {
                select(status);


                nextBuild = new Build() {
                    @Override
                    public Object build() {
                        ConfirmationDialog review_loan = new ConfirmationDialog("Repayment Loan", "You are about to continue this proces to the repayment phase on which this loan request will be marked as repayment, Are you sure?");
                        review_loan.getContinueBtn().addClickListener(f -> {
//                            setLoanRequestStatus(LoanRequest.Status.APPROVED);
                            updateNextStatus(loanRequestId, LoanRequest.Status.CLOSED);
                        });
                        return null;
                    }
                };
                backBuild = null;
            break;
            }
            case CLOSED -> {
                select(status);


                nextBuild = null;
                backBuild = null;
                break;
            }
        }*/
    }

    public void addTabListener(Executable executable){
        tabs.addSelectedChangeListener(f -> {
           executable.build();
        });
    }

    public LoanRequest.Status getStatus(){
        Tab selectedTab = tabs.getSelectedTab();

        LoanRequest.Status status = this.status;
       /* if(selectedTab.equals(requested)){
            status = LoanRequest.Status.REQUESTED;
        }
        if(selectedTab.equals(review)){
            status = LoanRequest.Status.REVIEW;
        }
        if(selectedTab.equals(input)){
            status = LoanRequest.Status.INPUT;
        }
        if(selectedTab.equals(eligible)){
            status = LoanRequest.Status.ELIGIBLE;
        }
        if(selectedTab.equals(approved)){
            status = LoanRequest.Status.APPROVED;
        }
        if(selectedTab.equals(provide)){
            status = LoanRequest.Status.PROVIDE;
        }
        if(selectedTab.equals(repayment)){
            status = LoanRequest.Status.REPAYMENT;
        }
        if(selectedTab.equals(closed)){
            status = LoanRequest.Status.CLOSED;
        }
*/
        if(this.status.compareTo(status) != 0){
            back.setVisible(false);
            next.setVisible(false);
        } else {
            select(this.status);
        }

        return status;
    }
    private LoanRequest.Status status;
    private void select(LoanRequest.Status status) {
        this.status = status;
        if(status.ordinal() < LoanRequest.Status.REQUESTED.ordinal()) {
            requested.setEnabled(false);
        } else {
            requested.setEnabled(true);
        }
        requested.setSelected(false);
        /*if(status.ordinal() < LoanRequest.Status.REVIEW.ordinal()) {
            review.setEnabled(false);
        } else {
            review.setEnabled(true);
        }
        if(status.ordinal() < LoanRequest.Status.INPUT.ordinal()) {
            input.setEnabled(false);
        } else {
            input.setEnabled(true);
        }
        if(status.ordinal() < LoanRequest.Status.ELIGIBLE.ordinal()) {
            eligible.setEnabled(false);
        } else {
            eligible.setEnabled(true);
        }*/
        review.setSelected(false);
        input.setSelected(false);
        eligible.setSelected(false);
        approved.setSelected(false);
        if(status.ordinal() < LoanRequest.Status.APPROVED.ordinal()) {
            approved.setEnabled(false);
        } else {
            approved.setEnabled(true);
        }
        provide.setSelected(false);
        /*if(status.ordinal() < LoanRequest.Status.PROVIDE.ordinal()) {
            provide.setEnabled(false);
        } else {
            provide.setEnabled(true);
        }
        if(status.ordinal() < LoanRequest.Status.REPAYMENT.ordinal()) {
            repayment.setEnabled(false);
        } else {
            repayment.setEnabled(true);
        }
        repayment.setSelected(false);
        if(status.ordinal() < LoanRequest.Status.CLOSED.ordinal()) {
            closed.setEnabled(false);
        } else {
            closed.setEnabled(true);
        }*/
        closed.setSelected(false);
        switch (status){
            case REQUESTED -> {
                requested.setEnabled(true);
                requested.setSelected(true);
                back.setVisible(false);
                next.setVisible(true);
                break;
            }
           /* case REVIEW -> {
                review.setEnabled(true);
                review.setSelected(true);
                back.setVisible(false);
                next.setVisible(true);
                break;
            }
            case INPUT -> {
                input.setEnabled(true);
                input.setSelected(true);
                back.setVisible(true);
                next.setVisible(true);
                break;
            }
            case ELIGIBLE -> {
                eligible.setEnabled(true);
                eligible.setSelected(true);
                back.setVisible(true);
                next.setVisible(true);
                break;
            }*/
            case APPROVED -> {
                approved.setSelected(true);
                approved.setEnabled(true);

                back.setVisible(false);
                next.setVisible(true);
                break;
            }
           /* case PROVIDE -> {
                provide.setSelected(true);
                provide.setEnabled(true);

                back.setVisible(false);
                next.setVisible(true);
                break;
            }
            case REPAYMENT -> {
                repayment.setEnabled(true);
                repayment.setSelected(true);
                back.setVisible(false);
                next.setVisible(true);
                break;
            }
            case CLOSED -> {
                closed.setEnabled(true);
                closed.setSelected(true);
                back.setVisible(false);
                next.setVisible(false);
                break;
            }*/
        }



    }

    private void updatePrevStatus(Long loanRequestId, LoanRequest.Status status) {
        LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
        LoanRequest loanRequest = loanRequestService.procesPrevStep(AuthenticatedUser.token(), loanRequestId, status);
        UI.getCurrent().getPage().reload();
    }

    private void updateNextStatus(Long loanRequestId, LoanRequest.Status status) {
        LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
        LoanRequest loanRequest = loanRequestService.procesNextStep(AuthenticatedUser.token(), loanRequestId, status);
        UI.getCurrent().getPage().reload();
    }
}
