const stats = db.getCollection("stats");

const browsers = stats.aggregate([{
  $project: {
    browserId: "$browserId",
    date: {
      $substr: ["$datetime", 0, 10]
    }
  }
},{
  $group: {
    _id: "$date",
    browsers: {
      $addToSet: "$browserId"
    }
  }
},{
  $sort: {
    _id: 1
  }
}]);

const paths = stats.aggregate([{
  $project: {
    path: "$path",
    date: {
      $substr: ["$datetime", 0, 10]
    }
  }
},{
  $group: {
    _id: {
      date: "$date",
      path: "$path"
    },
    count: {
      $sum: 1
    }
  }
},{
  $sort: {
    "_id.date": 1,
    "count": -1,
    "_id.path": 1
  }
}]);

printjson({
  browsers: browsers.toArray(),
  paths: paths.toArray()
});
