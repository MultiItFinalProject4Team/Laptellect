$(document).ready(function () {
    function convertEmptyToNull(value) {
            if (typeof value === 'undefined' || value === null) {
                return null;
            }
            return value.trim() === "" ? null : value;
        }


    function loadList(page) {
        let cate = convertEmptyToNull($(".form-select").val());
        let keyword = convertEmptyToNull($("#keyword").val());

        $.ajax({
            url: "/admin/product/list",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                page: page,
                size: 10,
                cate: cate,
                keyword: keyword
            }),
            success: function (response) {
                $("#product-list-box").html(response);
            },
            error: function (xhr, status, error) {
                console.error("조회 실패:", error);
            },
        });
    }

    $("#btn-search").on("click", function () {
        loadList(0);
    });

    $(document).on("click", ".pagination-controls button", function () {
        let page = $(this).data("page");
        loadList(page);
    });


    loadList(0);
});