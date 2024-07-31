package com.multi.laptellect.payment.model.dto;

public class TestDTO {
    private String username1;
    private String productname1;
    private String ProductDTO1;
    private String date_created1;
    private int productprice1;
    private String imd1;

    public TestDTO() {
    }

    public TestDTO(String username1, String productname1, String ProductDTO1, String date_created1, int productprice1, String imd1) {
        this.username1 = username1;
        this.productname1 = productname1;
        this.ProductDTO1 = ProductDTO1;
        this.date_created1 = date_created1;
        this.productprice1 = productprice1;
        this.imd1 = imd1;
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

    public String getProductDTO1() {
        return ProductDTO1;
    }

    public void setProductDTO1(String ProductDTO1) {
        this.ProductDTO1 = ProductDTO1;
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

    @Override
    public String toString() {
        return "TestDTO{" +
                "username1='" + username1 + '\'' +
                ", productname1='" + productname1 + '\'' +
                ", ProductDTO1='" + ProductDTO1 + '\'' +
                ", date_created1='" + date_created1 + '\'' +
                ", productprice1=" + productprice1 +
                ", imd1='" + imd1 + '\'' +
                '}';
    }
}
