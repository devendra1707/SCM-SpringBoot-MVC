console.log("This is script file")

const toggleSidebar = () => {

    if ($('.sidebar').is(":visible")) {
        // true
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%")

    }
    else {
        // false
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%")
    }
};

// Search  Start

// const search = () => {
//     // console.log("Searching......");
//     let query = $("#search-input").val();

//     if (query == '') {
//         $(".search-result").hide();
//     } else {
//         console.log(query);

//         // Sending request to server
//         let url = `http://localhost:8081/search/${query}`;

//         fetch(url).then((response) => {
//             return response.json();
//         })
//             .then((data) => {
//                 //data ......
//                 // console.log(data);

//                 let text = `<div class='list-group'> `

//                 data.array.forEach((contact) => {
//                     text += `<a href='#' class='list-group-item list-group-action'>${contact.name}  </a>`
//                 });

//                 text += `</div>`
//                 $(".search-resilt").html(text);
//                 $(".search-result").show();
//             })

//     }
// }



const search = () => {
    // console.log("searching...");
    let query = $("#search-input").val();
    console.log(query);
    if (query == "") {
        $(".search-result").hide();
    } else {
        console.log(query);
        // sending request to back end server
        // ->  (`) this is a back tick used below
        let url = `http://localhost:8281/search/${query}`;


        fetch(url).then((response) => {
            return response.json();
        })
            .then((data) => {
                //data........
                // console.log(data);
                //will create loop accodrgin to us
                let text = `<div class='list-group'>`;
                // this is array so will taverse with for each
                data.forEach((contact) => {
                    text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'> ${contact.name} </a>`
                });
                text += `</div>`;
                $(".search-result").html(text);

                $(".search-result").show();

            });

    }
};

//  Search End

