import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  FiCheck,
  FiX,
  FiPlus,
  FiFilter,
  FiSearch,
  FiEdit2,
  FiTrash2,
  FiChevronUp,
  FiChevronDown,
} from "react-icons/fi";
import DashbordLayout from "./layout/DashbordLayout";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

const BugManagementPage = () => {
  // State for bugs and UI
  const [bugs, setBugs] = useState([]);
  const [editingBug, setEditingBug] = useState(null);
  const [filteredBugs, setFilteredBugs] = useState([]);
  const [bugData, setBugData] = useState({});
  const [userRole, setUserRole] = useState("");
  const navigate = useNavigate();

  const [newBug, setNewBug] = useState({
    title: "",
    description: "",
    priority: "medium",
    status: "open",
  });
  const [isFormOpen, setIsFormOpen] = useState(false);

  // Filter and sort state
  const [filters, setFilters] = useState({
    status: "all",
    priority: "all",
    search: "",
  });

  const [sortConfig, setSortConfig] = useState({
    key: "createdAt",
    direction: "descending",
  });

  //fetch all bugs from database
  useEffect(() => {
    const token = localStorage.getItem("jwt");
    const decode = jwtDecode(token);
    if (!token) {
      return;
    }

    const fetchAllBugs = async () => {
      setUserRole(decode.authorities);

      const api = axios.create({
        baseURL: "http://localhost:8080",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await api.get("/api/bugs");
        console.log("Fetched bugs------->", response.data);
        setBugs(response.data);
      } catch (error) {
        console.log("faild to get bugs assign to user", error);
      }
    };

    fetchAllBugs();
  }, []);

  // Save to localStorage when bugs change
  useEffect(() => {
    localStorage.setItem("bugs", JSON.stringify(bugs));
  }, [bugs]);

  // Filter and sort bugs
  useEffect(() => {
    let results = [...bugs];

    // Apply filters
    if (filters.status !== "all") {
      results = results.filter((bug) => bug.status === filters.status);
    }
    if (filters.priority !== "all") {
      results = results.filter((bug) => bug.priority === filters.priority);
    }
    if (filters.search) {
      const searchTerm = filters.search.toLowerCase();
      results = results.filter(
        (bug) =>
          bug.title.toLowerCase().includes(searchTerm) ||
          bug.description.toLowerCase().includes(searchTerm)
      );
    }

    // Apply sorting
    if (sortConfig.key) {
      results.sort((a, b) => {
        if (a[sortConfig.key] < b[sortConfig.key]) {
          return sortConfig.direction === "ascending" ? -1 : 1;
        }
        if (a[sortConfig.key] > b[sortConfig.key]) {
          return sortConfig.direction === "ascending" ? 1 : -1;
        }
        return 0;
      });
    }

    setFilteredBugs(results);
  }, [bugs, filters, sortConfig]);

  // CRUD Operations
  const handleAddBug = () => {
    if (!newBug.title.trim()) return;

    const bug = {
      id: Date.now().toString(),
      ...newBug,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    setBugs((prev) => [...prev, bug]);
    setNewBug({
      title: "",
      description: "",
      priority: "medium",
      status: "open",
    });
    setIsFormOpen(false);
  };

  const updateBug = (id, updatedData) => {
    setBugs((prev) =>
      prev.map((bug) =>
        bug.id === id
          ? { ...bug, ...updatedData, updatedAt: new Date().toISOString() }
          : bug
      )
    );
    setEditingBug(null);
  };

  const deleteBug = (id) => {
    setBugs((prev) => prev.filter((bug) => bug.id !== id));
  };

  const updateBugStatus = (id, newStatus) => {
    setBugs((prev) =>
      prev.map((bug) =>
        bug.id === id
          ? { ...bug, status: newStatus, updatedAt: new Date().toISOString() }
          : bug
      )
    );
  };

  // UI Helpers
  const requestSort = (key) => {
    let direction = "ascending";
    if (sortConfig.key === key && sortConfig.direction === "ascending") {
      direction = "descending";
    }
    setSortConfig({ key, direction });
  };

  const getSeverityColor = (priority) => {
    switch (priority) {
      case "high":
        return "bg-red-500";
      case "medium":
        return "bg-orange-500";
      case "low":
        return "bg-green-500";
      default:
        return "bg-gray-500";
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "open":
        return "bg-red-100 text-red-800";
      case "in-progress":
        return "bg-blue-100 text-blue-800";
      case "resolved":
        return "bg-green-100 text-green-800";
      case "rejected":
        return "bg-gray-100 text-gray-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <DashbordLayout activeMenu="Manage Tasks">
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="max-w-7xl mx-auto">
          {/* Header */}
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold text-gray-800">Bug Tracker</h1>
            {/* <button
              onClick={() => setIsFormOpen(true)}
              className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <FiPlus className="mr-2" />
              Report Bug
            </button> */}
          </div>

          {/* Filters */}
          <div className="bg-white rounded-lg shadow p-4 mb-6">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <FiSearch className="text-gray-400" />
                </div>
                <input
                  type="text"
                  value={filters.search}
                  onChange={(e) =>
                    setFilters({ ...filters, search: e.target.value })
                  }
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2"
                  placeholder="Search bugs..."
                />
              </div>
              <select
                value={filters.status}
                onChange={(e) =>
                  setFilters({ ...filters, status: e.target.value })
                }
                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2"
              >
                <option value="ASSIGNED">Open</option>
                <option value="INPROGRESS">In Progress</option>
                <option value="REVIEW">Review</option>
                <option value="DONE">Done</option>
              </select>
              <select
                value={filters.priority}
                onChange={(e) =>
                  setFilters({ ...filters, priority: e.target.value })
                }
                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2"
              >
                <option value="all">All Severities</option>
                <option value="high">High</option>
                <option value="medium">Medium</option>
                <option value="low">Low</option>
              </select>
              <button
                onClick={() =>
                  setFilters({ status: "all", priority: "all", search: "" })
                }
                className="text-gray-500 hover:text-gray-700 flex items-center justify-center text-sm"
              >
                <FiFilter className="mr-1" />
                Clear Filters
              </button>
            </div>
          </div>

          {/* Bug Table */}
          <div className="bg-white shadow rounded-lg overflow-hidden">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th
                      scope="col"
                      className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                      onClick={() => requestSort("title")}
                    >
                      <div className="flex items-center">
                        Title
                        {sortConfig.key === "title" && (
                          <span className="ml-1">
                            {sortConfig.direction === "ascending" ? (
                              <FiChevronUp className="h-4 w-4" />
                            ) : (
                              <FiChevronDown className="h-4 w-4" />
                            )}
                          </span>
                        )}
                      </div>
                    </th>
                    <th
                      scope="col"
                      className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                    >
                      Description
                    </th>
                    <th
                      scope="col"
                      className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                      onClick={() => requestSort("priority")}
                    >
                      <div className="flex items-center">
                        Severity
                        {sortConfig.key === "priority" && (
                          <span className="ml-1">
                            {sortConfig.direction === "ascending" ? (
                              <FiChevronUp className="h-4 w-4" />
                            ) : (
                              <FiChevronDown className="h-4 w-4" />
                            )}
                          </span>
                        )}
                      </div>
                    </th>
                    <th
                      scope="col"
                      className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                      onClick={() => requestSort("status")}
                    >
                      <div className="flex items-center">
                        Status
                        {sortConfig.key === "status" && (
                          <span className="ml-1">
                            {sortConfig.direction === "ascending" ? (
                              <FiChevronUp className="h-4 w-4" />
                            ) : (
                              <FiChevronDown className="h-4 w-4" />
                            )}
                          </span>
                        )}
                      </div>
                    </th>

                    {(userRole.includes("ROLE_ADMIN") ||
                      userRole.includes("ROLE_MANAGER")) && (
                      <th
                        scope="col"
                        className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                      >
                        Actions
                      </th>
                    )}
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredBugs.length > 0 ? (
                    filteredBugs.map((bug) => (
                      <tr key={bug.id} className="hover:bg-gray-50" onClick={()=>{navigate(`/bugdetails/${bug.id}`)}}>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <div
                              className={`w-3 h-3 rounded-full ${getSeverityColor(
                                bug.priority
                              )} mr-3`}
                            />
                            <div className="font-medium text-gray-900">
                              {bug.title}
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className="text-sm text-gray-500 truncate max-w-xs">
                            {bug.description}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span
                            className={`px-2 py-1 text-xs font-medium rounded-full capitalize ${
                              bug.priority === "HIGH"
                                ? "bg-red-100 text-red-800"
                                : bug.priority === "MEDIUM"
                                ? "bg-orange-100 text-yellow-800"
                                : bug.priority === "LOW"
                                ? "bg-yellow-100 text-green-800"
                                : "bg-blue-100 text-blue-800"
                            }`}
                          >
                            {bug.priority}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            {/* <select
                              value={bug.status}
                              onChange={(e) =>
                                updateBugStatus(bug.id, e.target.value)
                              }
                              className={`text-sm font-medium rounded px-2 py-1 focus:outline-none capitalize ${getStatusColor(
                                bug.status
                              )}`}
                            >
                              <option value="ASSIGNED">Open</option>
                              <option value="INPROGRESS">In Progress</option>
                              <option value="REVIEW">Review</option>
                              <option value="DONE">Done</option>
                            </select> */}

                            <div
                              className={`text-sm font-medium rounded px-2 py-1 focus:outline-none capitalize ${getStatusColor(
                                bug.status
                              )}`}
                            >
                              {bug.status}
                            </div>
                          </div>
                        </td>

                        {(userRole.includes("ROLE_ADMIN") ||
                          userRole.includes("ROLE_MANAGER")) && (
                          <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium pl-5">
                          <button
                            onClick={() => deleteBug(bug.id)}
                            className="text-red-600 hover:text-red-900 mr-3 pl-3"
                          >
                            <FiTrash2 />
                          </button>
                          <button
                            onClick={() => setEditingBug(bug)}
                            className="text-blue-600 hover:text-blue-900"
                          >
                            <FiEdit2 />
                          </button>
                        </td>
                        )}
                        
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td
                        colSpan="5"
                        className="px-6 py-4 text-center text-sm text-gray-500"
                      >
                        No bugs found matching your criteria
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Add Bug Modal */}
          {isFormOpen && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
            >
              <motion.div
                initial={{ y: -50 }}
                animate={{ y: 0 }}
                className="bg-white rounded-lg shadow-xl w-full max-w-md"
              >
                <div className="p-6">
                  <div className="flex justify-between items-center mb-4">
                    <h3 className="text-lg font-semibold">Report New Bug</h3>
                    <button
                      onClick={() => setIsFormOpen(false)}
                      className="text-gray-400 hover:text-gray-500"
                    >
                      <FiX />
                    </button>
                  </div>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Title*
                      </label>
                      <input
                        type="text"
                        value={newBug.title}
                        onChange={(e) =>
                          setNewBug({ ...newBug, title: e.target.value })
                        }
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Description
                      </label>
                      <textarea
                        value={newBug.description}
                        onChange={(e) =>
                          setNewBug({ ...newBug, description: e.target.value })
                        }
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        rows="3"
                      />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Severity
                        </label>
                        <select
                          value={newBug.priority}
                          onChange={(e) =>
                            setNewBug({ ...newBug, priority: e.target.value })
                          }
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        >
                          <option value="high">High</option>
                          <option value="medium">Medium</option>
                          <option value="low">Low</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Status
                        </label>
                        <select
                          value={newBug.status}
                          onChange={(e) =>
                            setNewBug({ ...newBug, status: e.target.value })
                          }
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        >
                          <option value="ASSIGNED">Open</option>
                          <option value="INPROGRESS">In Progress</option>
                          <option value="REVIEW">Review</option>
                          <option value="DONE">Done</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div className="mt-6 flex justify-end space-x-3">
                    <button
                      type="button"
                      onClick={() => setIsFormOpen(false)}
                      className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50"
                    >
                      Cancel
                    </button>
                    <button
                      type="button"
                      onClick={handleAddBug}
                      className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                      disabled={!newBug.title.trim()}
                    >
                      Submit Bug
                    </button>
                  </div>
                </div>
              </motion.div>
            </motion.div>
          )}

          {/* Edit Bug Modal */}
          {editingBug && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
            >
              <motion.div
                initial={{ y: -50 }}
                animate={{ y: 0 }}
                className="bg-white rounded-lg shadow-xl w-full max-w-md"
              >
                <div className="p-6">
                  <div className="flex justify-between items-center mb-4">
                    <h3 className="text-lg font-semibold">Edit Bug</h3>
                    <button
                      onClick={() => setEditingBug(null)}
                      className="text-gray-400 hover:text-gray-500"
                    >
                      <FiX />
                    </button>
                  </div>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Title*
                      </label>
                      <input
                        type="text"
                        value={editingBug.title}
                        onChange={(e) =>
                          setEditingBug({
                            ...editingBug,
                            title: e.target.value,
                          })
                        }
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Description
                      </label>
                      <textarea
                        value={editingBug.description}
                        onChange={(e) =>
                          setEditingBug({
                            ...editingBug,
                            description: e.target.value,
                          })
                        }
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        rows="3"
                      />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Severity
                        </label>
                        <select
                          value={editingBug.priority}
                          onChange={(e) =>
                            setEditingBug({
                              ...editingBug,
                              priority: e.target.value,
                            })
                          }
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        >
                          <option value="critical">Critical</option>
                          <option value="high">High</option>
                          <option value="medium">Medium</option>
                          <option value="low">Low</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                          Status
                        </label>
                        <select
                          value={editingBug.status}
                          onChange={(e) =>
                            setEditingBug({
                              ...editingBug,
                              status: e.target.value,
                            })
                          }
                          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:ring-blue-500 focus:border-blue-500"
                        >
                          <option value="ASSIGNED">Open</option>
                          <option value="INPROGRESS">In Progress</option>
                          <option value="REVIEW">Review</option>
                          <option value="DONE">Done</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div className="mt-6 flex justify-end space-x-3">
                    <button
                      type="button"
                      onClick={() => setEditingBug(null)}
                      className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50"
                    >
                      Cancel
                    </button>
                    <button
                      type="button"
                      onClick={() => updateBug(editingBug.id, editingBug)}
                      className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                      Update Bug
                    </button>
                  </div>
                </div>
              </motion.div>
            </motion.div>
          )}
        </div>
      </div>
    </DashbordLayout>
  );
};

export default BugManagementPage;
