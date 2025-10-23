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
      .query("SELECT * FROM Users WHERE Username = @username AND PasswordHash = @password");



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

// POST /signup
app.post('/signup', async (req, res) => {
  try {
    const { usernames, passwords, dormitory } = req.body;

    if (!usernames || !passwords) {
      return res.status(400).json({ status: 'error', message: 'Username and password required' });
    }

    const pool = await poolPromise;

    // Check if username already exists
    const checkUser = await pool.request()
      .input('usernames', sql.NVarChar, usernames)
      .query('SELECT * FROM Users WHERE Username  = @usernames');

    if (checkUser.recordset.length > 0) {
      return res.status(409).json({ status: 'error', message: 'Username already exists' });
    }

    // Insert the new user
    await pool.request()
      .input('username', sql.NVarChar, usernames)
      .input('password', sql.NVarChar, passwords)
      .input('dorm', sql.NVarChar, dormitory)
      .query('INSERT INTO Users (Username, PasswordHash, DormName) VALUES (@username, @password, @dorm)');

    res.status(201).json({ status: 'success', message: 'User created successfully' });

  } catch (error) {
    console.error('Signup error:', error);
    res.status(500).json({ status: 'error', message: 'Internal server error' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`API running on port ${PORT}`));