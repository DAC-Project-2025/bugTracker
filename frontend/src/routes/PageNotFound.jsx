import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

const PageNotFound = () => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
      className="h-screen flex items-center justify-center bg-gray-50"
    >
      <div className="text-center">
        <motion.h1
          initial={{ y: -50 }}
          animate={{ y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="text-5xl font-bold text-gray-800 mb-4"
        >
          404
        </motion.h1>
        <motion.p
          initial={{ y: 50 }}
          animate={{ y: 0 }}
          transition={{ duration: 0.5, delay: 0.4 }}
          className="text-lg text-gray-600 mb-8"
        >
          Page Not Found
        </motion.p>
        <Link to="/">
          <motion.button
            initial={{ scale: 0.5 }}
            animate={{ scale: 1 }}
            transition={{ duration: 0.5, delay: 0.6 }}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Back to Home
          </motion.button>
        </Link>
      </div>
    </motion.div>
  );
};

export default PageNotFound;
