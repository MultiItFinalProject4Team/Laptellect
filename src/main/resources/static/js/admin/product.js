$(document).ready(function () {
    function convertEmptyToNull(value) {
            if (typeof value === 'undefined' || value === null) {
                return null;
            }
            return value.trim() === "" ? null : value;
        }

    function formatPrices() {
        $('.price').each(function() {
            let value = $(this).text().replace(' 원', '').replace(/,/g, '');
            let formattedValue = new Intl.NumberFormat().format(value);
            $(this).text(formattedValue + ' 원');
        });
    }
    function checkBoxSelect(){
        $(document).on("click", "#select-all", function() {
                   $(".select-item").prop('checked', $(this).prop('checked'));
               });

        $(document).on("click", ".select-item", function() {
            if ($(".select-item:checked").length === $(".select-item").length) {
                $("#select-all").prop('checked', true);
            } else {
                $("#select-all").prop('checked', false);
            }
        });
    }

    $("#btn-search").on("click", function () {
        loadList(0);
    });

    function loadList(page) {
        let cate = convertEmptyToNull($("#searchCategory").val());
        let keyword = convertEmptyToNull($("#searchInput").val());

        switch(keyword) {
            case "노트북":
                keyword = 1;
                break;
            case "마우스":
                keyword = 2;
                break;
            case "키보드":
                keyword = 3;
                break;
            default:
                keyword = keyword;
                break;
        }

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
                formatPrices();
                checkBoxSelect();
            },
            error: function (xhr, status, error) {
                console.error("조회 실패:", error);
            },
        });
    }
    
    function productSelectedDelete() {
        let selectProduct = [];
        $(".select-item:checked").each(function() {
            selectProduct.push($(this).val());
        });
        console.log(selectProduct);
        if(selectProduct.length == 0) {
            swal("알림", "삭제할 항목을 선택하세요.", "info");
            return;
        }

        if(!confirm("선택된 항목을 삭제하시겠습니까?")) {

            return;

        }

         $(".select-item:checked").closest("tr").addClass("deleted-item");
         $(".select-item:checked").closest("tr").find("a").removeAttr("href");

        $.ajax({
            url:"/admin/product/delete",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(selectProduct),
            success: function(response){
                swal("완료", "삭제가 완료되었습니다.", "success");
                loadList(0);
            },
            error: function (xhr, status, error) {
                console.error("삭제 실패:", error);
            },
        });
    }

    function restoreSelectedProducts() {
        let selectProduct = [];
        $(".select-item:checked").each(function() {
            selectProduct.push($(this).val());
        });

        if (selectProduct.length == 0) {
            alert("복원할 항목을 선택하세요.");
            return;
        }

        $.ajax({
            url: "/admin/product/restore",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(selectProduct),
            success: function(response) {
                alert("복원이 완료되었습니다.");
                loadList(0);
            },
            error: function (xhr, status, error) {
                console.error("복원 실패:", error);
            },
        });
    }

    $("#btn-search").on("click", function () {
        loadList(0);
    });

    $("#btn-delete").on("click", function() {
        productSelectedDelete();
    });

    $("#btn-restore").on("click", function() {
        restoreSelectedProducts();
    });

    $(document).on("click", ".pagination-controls button", function () {
        let page = $(this).data("page");
        loadList(page);
    });


    loadList(0);
});