/* global fetch */
function showArticles() {
    fetch('./OnShowArticles',
            {
                method: 'get',

                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
            .then(function (response) {
                response.json().then(function (data)
                {
                    console.log(data);
                    var table = "<table>";
                    table += "<tr><th>Name</th><th>Price</th><th>Amount</th></tr>";
                    for (var i = 0; i < data.length; i++)
                    {
                        table += "<tr>";
                        table += "<td>" + data[i]['artname'] + "</td>";
                        table += "<td>" + data[i]['price'] + "</td>";
                        table += "<td>" + data[i]['amount'] + "</td>";
                        table += "<td><input type='button' name='add' value='+' onClick='updateAmount("+ data[i]['articleid'] +",1)'/></td>";
                        table += "<td><input type='button' name='add' value='-' onClick='updateAmount("+ data[i]['articleid'] +",-1)'/></td>";
                        table += "</tr>";
                    }
                    table += "</table>";

                    document.getElementById("article").innerHTML = table;
                });
            });
}

function updateAmount(articleid, amount) {
    console.log(articleid);
    console.log(amount);
    
    fetch('./UpdateAmountServlet',
            {
                method: 'post',

                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: {
                    articleid: articleid,
                    amount: amount
                }
            })
            .then(function (response) {
                response.json().then(function (data)
                {
                   console.log(data); 
                });
            });
}
function addArticle(articleid)
{
    updateAmount(articleid,1);
}
function removeArticle(articleid)
{
    updateCart(articleid,-1);
}