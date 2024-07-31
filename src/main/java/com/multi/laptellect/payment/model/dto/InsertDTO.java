package com.multi.laptellect.payment.model.dto;

public class InsertDTO {
    private String username2;
    private String productname2;
    private String ProductDTO2;
    private int productprice2;
    private String date_created2;
    private String imd2;

    public InsertDTO() {

    }

    public InsertDTO(String username2, String productname2, String ProductDTO2, int productprice2, String date_created2, String imd2) {
        this.username2 = username2;
        this.productname2 = productname2;
        this.ProductDTO2 = ProductDTO2;
        this.productprice2 = productprice2;
        this.date_created2 = date_created2;
        this.imd2 = imd2;
    }

    public String getUsername2() {
        return username2;
    }

    public void setUsername2(String username2) {
        this.username2 = username2;
    }

    public String getProductname2() {
        return productname2;
    }

    public void setProductname2(String productname2) {
        this.productname2 = productname2;
    }

    public String getProductDTO2() {
        return ProductDTO2;
    }

    public void setProductDTO2(String ProductDTO2) {
        this.ProductDTO2 = ProductDTO2;
    }

    public int getProductprice2() {
        return productprice2;
    }

    public void setProductprice2(int productprice2) {
        this.productprice2 = productprice2;
    }

    public String getDate_created2() {
        return date_created2;
    }

    public void setDate_created2(String date_created2) {
        this.date_created2 = date_created2;
    }

    public String getImd2() {
        return imd2;
    }

    public void setImd2(String imd2) {
        this.imd2 = imd2;
    }

    @Override
    public String toString() {
        return "InsertDTO{" +
                "username2='" + username2 + '\'' +
                ", productname2='" + productname2 + '\'' +
                ", ProductDTO2='" + ProductDTO2 + '\'' +
                ", productprice2=" + productprice2 +
                ", date_created2='" + date_created2 + '\'' +
                ", imd2='" + imd2 + '\'' +
                '}';
    }
}
