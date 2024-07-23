package com.multi.laptellect.payment.model.dto;

public class OrderlistDTO {
    private String username1;
    private String productname1;
    private String productinfo1;
    private String date_created1;
    private int productprice1;
    private String imd1;
    private String refund1;
    private String refund_date1;

    public OrderlistDTO() {
    }

    public OrderlistDTO(String username1, String productname1, String productinfo1, String date_created1, int productprice1, String imd1, String refund1, String refund_date1) {
        this.username1 = username1;
        this.productname1 = productname1;
        this.productinfo1 = productinfo1;
        this.date_created1 = date_created1;
        this.productprice1 = productprice1;
        this.imd1 = imd1;
        this.refund1 = refund1;
        this.refund_date1 = refund_date1;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getProductname1() {
        return productname1;
    }

    public void setProductname1(String productname1) {
        this.productname1 = productname1;
    }

    public String getProductinfo1() {
        return productinfo1;
    }

    public void setProductinfo1(String productinfo1) {
        this.productinfo1 = productinfo1;
    }

    public String getDate_created1() {
        return date_created1;
    }

    public void setDate_created1(String date_created1) {
        this.date_created1 = date_created1;
    }

    public int getProductprice1() {
        return productprice1;
    }

    public void setProductprice1(int productprice1) {
        this.productprice1 = productprice1;
    }

    public String getImd1() {
        return imd1;
    }

    public void setImd1(String imd1) {
        this.imd1 = imd1;
    }

    public String getRefund1() {
        return refund1;
    }

    public void setRefund1(String refund1) {
        this.refund1 = refund1;
    }

    public String getRefund_date1() {
        return refund_date1;
    }

    public void setRefund_date1(String refund_date1) {
        this.refund_date1 = refund_date1;
    }

    @Override
    public String toString() {
        return "OrderlistDTO{" +
                "username1='" + username1 + '\'' +
                ", productname1='" + productname1 + '\'' +
                ", productinfo1='" + productinfo1 + '\'' +
                ", date_created1='" + date_created1 + '\'' +
                ", productprice1=" + productprice1 +
                ", imd1='" + imd1 + '\'' +
                ", refund1='" + refund1 + '\'' +
                ", refund_date1='" + refund_date1 + '\'' +
                '}';
    }
}
