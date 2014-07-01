$(document).ready(function(){
    var parts = document.location.pathname.split("/");
    $("#" + parts[1]).addClass("active");
    
    $(".delete-link").click(function(e){
        e.preventDefault();
        $("#delete").attr("href", $(this).attr("path"));
    });
    
    bindShowSegment();
    
    bindAddSegment();
    
    bindBookingSearch();
    
});

var deleteSegmentRow = function() {
    $(this).parent().parent().remove();
}

function bindShowSegment() {
    $('a[id$="show-segment"]').click(function(){
        var element = "." + $(this).attr("id").split("-")[0]+"-segment";
        if ($(element).hasClass("hide")) {
            $(element).removeClass("hide");
            $(this).html("<i class=\"icon-arrow-up\"></i>Hide");
        } else {
            $(element).addClass("hide");
            $(this).html("<i class=\"icon-arrow-down\"></i>Show");
        }
    });
}

function bindBookingSearch() {
    $('#get-flights').click(function(e) {
        $.post('/booking/flights', {
            origin: $('#origin').val(),
            destination: $('#destination').val(),
            class_id: $('#class').val(),
            date: $('#date').val()
        }, function(data) {
            $('#flights').html(data);
            bindBook();
        });
        e.preventDefault();
    });
}

function bindAddSegment() {
    $("#add-segment").click(function() {
        var n = $('table tr').length - 1;
        var origin = $('select[id$="airpt_id_from"]').first().clone();
        origin.attr("name","segments[" + n + "].airpt_id_from");
        origin.attr("id","segments[" + n + "].airpt_id_from");
        origin.attr("class", "input-small");
        var dest = $('select[id$="airpt_id_to"]').first().clone();
        dest.attr("name","segments[" + n + "].airpt_id_to");
        dest.attr("id","segments[" + n + "].airpt_id_to");
        dest.attr("class", "input-small");
        
        $('table > tbody:last').append('<tr><td><input type="text" class="input-small" name="segments[' + n + '].arr_time" id="segments[' + n + '].arr_time"></td>' 
            + '<td><input type="text" class="input-small" name="segments[' + n + '].dep_time" id="segments[' + n + '].dep_time"></td>' 
            + '<td>' + $('<div>').append(origin).html() + '</td>' 
            + '<td>' + $('<div>').append(dest).html() + '</td>' 
            + '<td><a class="btn delete-segment"><i class="icon-remove"></i> Delete</a></td></tr>');
        $('.delete-segment').unbind('click', deleteSegmentRow);
        $('.delete-segment').bind('click', deleteSegmentRow);
    });
}

function bindBook() {
    $(".book").click(function(e){
        e.preventDefault();
        var flightid = $($($(this)[0]).parent().siblings()[1]).html();
        $.get("/booking/save", {
            class_id: $('#class').val(),
            customer: $('#customer-id').val(),
            flight_id: flightid
        }, function(){
            window.location.href = "/booking";
        });
    });
}