function showArticles(orderid) {
    fetch('./ShowDetailServlet',
            {
                method: 'post',

                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    orderid: orderid
                })
            })
            .then(function (response) {
                response.json().then(function (data)
                {
                    console.log(data);
                    var table = "<table>";
                    table += "<tr><th>Article name</th><th>Price per article</th><th>Amount</th><th>Total price</th></tr>";
                    for(var i = 0; i < data.length; i++)
                    {
                        table += "<tr><td>"+data[i]["artname"]+"</td><td>"+data[i]["price"]+"</td><td>"+data[i]["amount"]+"</td><td>"+data[i]["totalprice"]+"</td></tr>";
                    }
                    table+="</table>";
                    document.getElementById("details").innerHTML = table;
                });
            });
}