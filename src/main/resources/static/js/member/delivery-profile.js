$(function () {
      $(document).ready(function () {
        $(".error-message").hide();
        $(".addAddressBtn").prop("disabled", true);

        $("#address-name").on("blur", function () {
          let addressName = $(this).val();
          console.log(addressName);

          if(addressName !== "") {
            $(".addAddressBtn").prop("disabled", false);
            $("#addressError").hide();
          } else {
            $(".addAddressBtn").prop("disabled", true);
            $("#addressError").show();
          }
        });

        $(".addAddressBtn").on("click", function () {
          function convertEmptyToNull(value) {
                  return value.trim() === "" ? null : value;
          }

          let addressName = convertEmptyToNull($('#address-name').val());
          let recipientName = convertEmptyToNull($('#recipient-name').val());
          let postalCode = convertEmptyToNull($('#postal-code').val());
          let address = convertEmptyToNull($('#address').val());
          let detailAddress = convertEmptyToNull($('#detail-address').val());
          let tel = convertEmptyToNull($('#recipient-phone').val());
          let request = convertEmptyToNull($('#request').val());

          console.log(addressName, recipientName, postalCode, address, detailAddress, tel, request);

          $.ajax({
              url: "/api/member/insert-address",
              type: "POST",
              contentType: "application/json",
              data: JSON.stringify({
                  addressName: addressName,
                  recipientName: recipientName,
                  postalCode: postalCode,
                  address: address,
                  detailAddress: detailAddress,
                  tel: tel,
                  request: request
              }),
              success: function (response) {
                  switch(response) {
                    case 1:
                        alert("배송지 등록 성공");
                        break;
                    case 0:
                        alert("배송지 이름을 입력해주세요.");
                    case 500:
                        alert("배송지 등록 실패.");
                  }
              },
              error: function () {
                  alert("배송지 등록 실패");
              }
          });
        });
      });
    });