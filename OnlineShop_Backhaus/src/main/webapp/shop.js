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
                body: JSON.stringify({
                    articleid: articleid,
                    amount: amount
                })
            })
            .then(function (response) {
                response.json().then(function (data)
                {
                   console.log(data); 
                   document.getElementById("amount"+articleid).innerHTML=data["amount"];
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