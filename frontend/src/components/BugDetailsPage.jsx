import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { formatDistanceToNow } from "date-fns";
import axios from "axios";
import { useParams } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import DashbordLayout from "./layout/DashbordLayout";

const BugDetailsPage = () => {
  const { id } = useParams();
  const [userRole, setUserRole] = useState();
  const [userId, setUserId] = useState();
  const [reporterId, setReporterId] = useState();
  const [bugData, setBugData] = useState({});

  const [userData, setUserData] = useState();
  const [reporterData, setReporterData] = useState();
  const [commentsData, setCommentsData] = useState([]);
  const [bug, setBug] = useState({
    id: "BUG-1024",
    title: "Login page crashes on Safari browsers",
    description:
      "When users attempt to login using Safari, the page crashes with a white screen. This happens on both desktop and mobile versions of Safari.",
    status: "open",
    priority: "high",
    created: "2023-04-15T14:32:00",
    reporter: { name: "Alice Johnson", avatar: "AJ" },
    assignee: { name: "David Chen", avatar: "DC", online: true },
    environment: "Production",
    browser: "Safari 16.4",
    os: "iOS 16/macOS Ventura",
    attachments: [
      {
        id: 1,
        name: "screenshot-crash.png",
        size: "2.4 MB",
        type: "image",
        url: "#",
      },
      {
        id: 2,
        name: "console-errors.log",
        size: "158 KB",
        type: "text",
        url: "#",
      },
    ],
  });

  const [comments, setComments] = useState([
    {
      id: 1,
      author: { name: "David Chen", avatar: "DC", online: true },
      content: [
        "I've reproduced the issue. It seems to be related to the authentication library we're using.",
        <span key="code" className="bg-gray-100 font-mono text-sm p-1 rounded">
          AuthProvider.js: line 42
        </span>,
      ],
      timestamp: "2023-04-15T15:45:00",
    },
    {
      id: 2,
      author: { name: "You", avatar: "YO", online: true },
      content:
        "Thanks for checking. Any idea when we can expect a fix? The client is asking for an ETA.",
      timestamp: "2023-04-15T16:20:00",
    },
  ]);

  const [newComment, setNewComment] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Floating action menu state
  const [actionMenuOpen, setActionMenuOpen] = useState(false);

  // UI Helpers
  const getPriorityColor = () => {
    const colors = {
      HIGH: "from-red-500 to-pink-600",
      MEDIUM: "from-amber-500 to-orange-600",
      LOW: "from-green-500 to-emerald-600",
    };
    return colors[bugData.priority] || "from-gray-500 to-gray-600";
  };

  const getStatusColor = () => {
    const colors = {
      open: "bg-blue-100 text-blue-800",
      "in-progress": "bg-purple-100 text-purple-800",
      resolved: "bg-teal-100 text-teal-800",
      closed: "bg-gray-100 text-gray-800",
    };
    return colors[bug.status] || "bg-gray-100 text-gray-800";
  };

  const getFileIcon = (type) => {
    const icons = {
      image: "🖼️",
      text: "📄",
      pdf: "📝",
      zip: "🗄️",
    };
    return icons[type] || "📁";
  };

  // Handlers
  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;

    setIsSubmitting(true);

    const token = localStorage.getItem("jwt");
    const commentApi = axios.create({
      baseURL: "http://localhost:8082",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    try {
      const payload = {
        bugId: id, // current bug id
        userId: userData.id, // logged-in user id
        message: newComment,
      };

      const commentRes = await commentApi.post("/api/comments/add", payload);

      // Use API response so you get actual ID, timestamp, etc.
      const savedComment = commentRes.data;
      setCommentsData((prev) => [...prev, commentRes.data]);
      setComments((prev) => [...prev, savedComment]);

      setNewComment(""); // clear input
    } catch (error) {
      console.error("Failed to add comment:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const addReaction = (commentId, emoji) => {
    setComments(
      comments.map((comment) => {
        if (comment.id !== commentId) return comment;

        const existingReaction = comment.reactions.find(
          (r) => r.emoji === emoji
        );
        if (existingReaction) {
          return {
            ...comment,
            reactions: comment.reactions.map((r) =>
              r.emoji === emoji ? { ...r, count: r.count + 1 } : r
            ),
          };
        }

        return {
          ...comment,
          reactions: [...comment.reactions, { emoji, count: 1 }],
        };
      })
    );
  };

  useEffect(() => {
    const token = localStorage.getItem("jwt");
    const decode = jwtDecode(token);
    setUserRole(decode.authorities || "");

    const api = axios.create({
      baseURL: "http://localhost:8080",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    const commentApi = axios.create({
      baseURL: "http://localhost:8082",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    const userApi = axios.create({
      baseURL: "http://localhost:8081",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    const fetchAllData = async () => {
      try {
        // 1️⃣ Get bug details
        const bugRes = await api.get(`/api/bugs/${id}`);
        const bug = bugRes.data;
        console.log("Bug details:", bug);

        setBugData(bug);

        // 2️⃣ Get user & reporter in parallel
        const [userRes, reporterRes] = await Promise.all([
          userApi.get(`/api/user/users/${bug.assignedUserId}`),
          userApi.get(`/api/user/users/${bug.reporter}`),
        ]);

        setUserData(userRes.data);
        setReporterData(reporterRes.data);

        // 3️⃣ Get comments
        const commentsRes = await commentApi.get(`/api/comments/bug/${id}`);
        console.log("Comments:", commentsRes.data);
        setCommentsData(commentsRes.data);
      } catch (error) {
        console.error("Error fetching bug/user/reporter/comments:", error);
      }
    };

    fetchAllData();
  }, [id]);

  return (
    <DashbordLayout>
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {console.log("userId--------->", userId)}
      {console.log("user data--------->", userData)}
      {console.log("reporter data--------->", reporterData)}

      <main className="max-w-7xl mx-auto px-4 py-6">
        <div className="flex flex-col lg:flex-row gap-6">
          {/* Main Content */}
          <div className="flex-1 space-y-6">
            {/* Bug Header */}
            <motion.div
              initial={{ y: -20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              className="bg-white rounded-2xl shadow-xl overflow-hidden"
            >
              <div className="p-6">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="flex items-center space-x-3">
                      <span
                        className={`text-xs font-medium px-2.5 py-0.5 rounded-full ${getStatusColor()}`}
                      >
                        {bug.status.replace("-", " ")}
                      </span>
                      <span
                        className={`text-xs font-medium bg-gradient-to-r ${getPriorityColor()} text-white px-2.5 py-0.5 rounded-full`}
                      >
                        {bugData?.priority
                          ? `${bugData.priority} Priority`
                          : "Loading..."}
                      </span>
                    </div>
                    <h1 className="text-2xl md:text-3xl font-bold text-gray-900 mt-3">
                      {bug.title}
                    </h1>
                  </div>

                  {/* Floating action button */}
                  {/* <div className="relative">
                    <button
                      onClick={() => setActionMenuOpen(!actionMenuOpen)}
                      className="w2 h2 p-2 rounded-full bg-gradient-to-r bg-gray-300 text-black  shadow-lg hover:shadow-xl transition-all"
                    >
                      <svg
                        className="w-5 h-5"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"
                        />
                      </svg>
                    </button>

                    <AnimatePresence>
                      {actionMenuOpen && (
                        <motion.div
                          initial={{ opacity: 0, y: 20 }}
                          animate={{ opacity: 1, y: 0 }}
                          exit={{ opacity: 0, y: 20 }}
                          transition={{
                            type: "spring",
                            stiffness: 300,
                            damping: 20,
                          }}
                          className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 z-10"
                        >
                          <div className="py-1">
                            <button className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                              Edit Bug
                            </button>
                            <button className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                              Change Status
                            </button>
                            <button className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100">
                              Delete Bug
                            </button>
                          </div>
                        </motion.div>
                      )}
                    </AnimatePresence>
                  </div> */}
                </div>

                <div className="mt-4">
                  <div className="prose prose-sm max-w-none text-gray-600">
                    <p className="text-gray-800 font-medium">Description</p>
                    <p>{bug.description}</p>
                  </div>
                </div>

                <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Reporter
                    </p>
                    <div className="flex items-center mt-1">
                      <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-800 font-medium mr-2">
                        {bug.reporter.avatar}
                      </div>
                      <span className="font-medium text-gray-900">
                        {reporterData?.name || "Loading..."}
                      </span>
                    </div>
                  </div>

                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Assignee
                    </p>
                    <div className="flex items-center mt-1">
                      <div className="relative">
                        <div className="w-8 h-8 rounded-full bg-purple-100 flex items-center justify-center text-purple-800 font-medium mr-2">
                          {bug.assignee.avatar}
                        </div>
                        <span
                          className={`absolute -bottom-1 -right-1 w-3 h-3 rounded-full ${
                            bug.assignee.online ? "bg-green-500" : "bg-gray-300"
                          } border-2 border-white`}
                        ></span>
                      </div>
                      <span className="font-medium text-gray-900">
                        {userData?.name || "Loading user..."}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Environment
                    </p>
                    <p className="mt-1 font-medium">
                      {bugData?.environment || "Loading..."}
                    </p>

                    {console.log("bug data ---------->", bugData)}
                  </div>
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Browser
                    </p>
                    <p className="mt-1 font-medium">
                      {bugData?.browser || "Loading"}
                    </p>
                  </div>
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      OS
                    </p>
                    <p className="mt-1 font-medium">
                      {bugData?.os || "Loading"}
                    </p>
                  </div>
                </div>
              </div>
            </motion.div>

            {/* Attachments */}
            {bugData?.files?.length > 0 && (
              <motion.div
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.1 }}
                className="bg-white rounded-2xl shadow-xl overflow-hidden"
              >
                <div className="p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                    Attachments
                    <span className="ml-2 bg-gray-100 text-gray-800 text-xs font-medium px-2 py-0.5 rounded-full">
                      {bugData?.files?.length || 0}
                    </span>
                  </h2>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {bugData?.files?.map((file) => (
                      <motion.div
                        key={file.id}
                        whileHover={{ y: -2 }}
                        className="border border-gray-100 rounded-lg p-3 hover:shadow-md transition-all cursor-pointer"
                      >
                        <div className="flex items-start space-x-3">
                          <div className="bg-blue-50 p-2 rounded-lg text-2xl">
                            {getFileIcon(file.type)}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-gray-900 truncate">
                              {file.name}
                            </p>
                            <p className="text-xs text-gray-500 mt-0.5">
                              {file.size}
                            </p>
                            <button
                              className="mt-2 text-xs font-medium text-blue-600 hover:text-blue-800 transition"
                              onClick={(e) => {
                                e.stopPropagation();
                                // Download logic here
                              }}
                            >
                              Download
                            </button>
                          </div>
                        </div>
                      </motion.div>
                    ))}
                  </div>
                </div>
              </motion.div>
            )}

            {/* Comments */}
            <motion.div
              initial={{ y: 20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ delay: 0.2 }}
              className="bg-white rounded-2xl shadow-xl overflow-hidden"
            >
              <div className="p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">
                  Comments
                </h2>
                {console.log("comments data -------->", commentsData)}
                <div className="space-y-6">
                  {commentsData.map((comment) => (
                    <div
                      key={comment.id}
                      className="flex items-start space-x-3 group"
                    >
                      <div className="relative flex-shrink-0">
                        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-100 to-blue-100 flex items-center justify-center text-purple-800 font-medium">
                          {/* {comment.author.avatar} */}AJ
                        </div>
                      </div>

                      <div className="flex-1 min-w-0">
                        <div className="bg-gray-50 rounded-2xl p-4">
                          <div className="flex justify-between items-baseline">
                            <span className="font-medium text-gray-900">
                              {comment?.userName || "Loading..."}
                            </span>
                            <span className="text-xs text-gray-500">
                              {formatDistanceToNow(
                                new Date(comment.timestamp),
                                { addSuffix: true }
                              )}
                            </span>
                          </div>

                          <div className="mt-1.5 text-gray-700">
                            {Array.isArray(comment.message)
                              ? comment.message.map((c, i) => (
                                  <div key={i}>{c}</div>
                                ))
                              : comment.message}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Add Comment */}
                <form onSubmit={handleCommentSubmit} className="mt-6">
                  <div className="flex items-start space-x-3">
                    <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-800 font-medium flex-shrink-0">
                      YO
                    </div>

                    <div className="flex-1 relative">
                      <textarea
                        rows={3}
                        className="block w-full border-gray-300 shadow-sm rounded-xl bg-gray-50 focus:ring-blue-500 focus:border-blue-500 text-gray-700 placeholder:text-gray-400"
                        placeholder="Add a comment..."
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                      />

                      <div className="mt-3 flex justify-end">
                        <motion.button
                          type="submit"
                          className="px-4 py-2 bg-gradient-to-r from-blue-600 to-purple-600 text-white font-medium rounded-lg shadow hover:shadow-md transition-all disabled:opacity-70"
                          disabled={!newComment.trim() || isSubmitting}
                          whileTap={{ scale: 0.98 }}
                        >
                          {isSubmitting ? "Posting..." : "Post Comment"}
                        </motion.button>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </motion.div>
          </div>

          {/* Sidebar */}
          <div className="lg:w-80 space-y-6">
            <motion.div
              initial={{ y: 20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ delay: 0.3 }}
              className="bg-white rounded-2xl shadow-xl overflow-hidden"
            >
              <div className="p-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">
                  Details
                </h2>

                <div className="space-y-4">
                  <div>
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Created
                    </p>
                    <p className="mt-1 font-medium">
                      {bugData?.createdAt
                        ? formatDistanceToNow(new Date(bugData.createdAt), {
                            addSuffix: true,
                          })
                        : "Loading..."}
                    </p>
                  </div>

                  <div>
                    <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </p>
                    <select
                      className="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3"
                      value={bug.status}
                      onChange={(e) =>
                        setBug({ ...bug, status: e.target.value })
                      }
                    >
                      <option value="ASSIGNED">Open</option>
                      <option value="INPROGRESS">In Progress</option>
                      {(userRole === "ROLE_ADMIN" ||
                    userRole === "ROLE_MANAGER") && (<option value="REASSIGNED">Reassigned</option>)}
                      <option value="REVIEW">Review</option>
                      <option value="DONE">Closed</option>
                    </select>
                  </div>

                  {(userRole === "ROLE_ADMIN" ||
                    userRole === "ROLE_MANAGER") && (
                    <div>
                      <div>
                        <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Priority
                        </p>
                        <select
                          className="mt-1 block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3"
                          value={bug.priority}
                          onChange={(e) =>
                            setBug({ ...bug, priority: e.target.value })
                          }
                        >
                          <option value="LOW">Low</option>
                          <option value="MEDIUM">Medium</option>
                          <option value="HIGH">High</option>
                        </select>
                      </div>
                      <div>
                        <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Assign To
                        </p>
                        <div className="mt-1">
                          <div className="relative">
                            <select
                              className="block w-full rounded-lg border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 bg-gray-50 py-2 px-3"
                              value={bug.assignee.name}
                              onChange={(e) =>
                                setBug({
                                  ...bug,
                                  assignee: {
                                    ...bug.assignee,
                                    name: e.target.value,
                                    // In a real app, update avatar too
                                  },
                                })
                              }
                            >
                              <option>Unassigned</option>
                              <option>David Chen</option>
                              <option>Alice Johnson</option>
                              <option>Sam Wilson</option>
                            </select>
                          </div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </motion.div>
          </div>
        </div>
      </main>
    </div>
    </DashbordLayout>
  );
};

export default BugDetailsPage;
