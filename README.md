Proyecto Salvo

// Modelo de request al metodo addSalvo

$.post({
      url: "/api/games/players/"id de Gameplayer"/salvoes",
      data: JSON.stringify({salvoLocations: ["D1", "D2", "D3"]}),
      dataType: "text",
      contentType: "application/json"
  }).done(function(data){console.log(data)})