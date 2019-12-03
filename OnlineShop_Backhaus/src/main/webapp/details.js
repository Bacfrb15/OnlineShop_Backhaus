function showArticles() {
    fetch('./OnShowArticles',
            {
                method: 'get',

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
                    
                });
            });
}