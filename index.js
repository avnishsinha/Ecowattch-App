const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const { sql, poolPromise } = require("./db");

const app = express();
app.use(cors());
app.use(bodyParser.json());

app.post("/login", async (req, res) => {
  const { usernames, passwords } = req.body;

  try {
    const pool = await poolPromise;
    const result = await pool
      .request()
      .input("username", sql.NVarChar, usernames)
      .input("password", sql.NVarChar, passwords)
      .query("SELECT * FROM LogIns WHERE usernames = @username AND passwords = @password");



    if (result.recordset.length > 0) {
      res.json({ status: "success", user: result.recordset[0] });
    } else {
      res.json({ status: "failure", message: "Invalid credentials" });
    }
  } catch (err) {
    console.error("Login error", err);
    res.status(500).json({ status: "error", message: "Server error" });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`API running on port ${PORT}`));