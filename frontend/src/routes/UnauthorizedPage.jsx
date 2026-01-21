import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

const UnauthorizedPage = () => {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.8 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.6, ease: "easeOut" }}
      className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-r from-red-400 via-red-500 to-red-600 px-4"
    >
      <motion.div
        initial={{ y: -50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.3, duration: 0.6 }}
        className="bg-white rounded-xl shadow-lg p-10 max-w-md w-full text-center"
      >
        <h1 className="text-6xl font-extrabold text-red-600 mb-4">401</h1>
        <h2 className="text-2xl font-semibold mb-2 text-gray-800">Unauthorized</h2>
        <p className="text-gray-600 mb-6">
          You do not have permission to access this page.
        </p>
        <Link to="/">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="px-6 py-3 bg-red-600 text-white rounded-lg font-semibold shadow hover:bg-red-700 transition"
          >
            Go to Home
          </motion.button>
        </Link>
      </motion.div>
    </motion.div>
  );
};

export default UnauthorizedPage;
