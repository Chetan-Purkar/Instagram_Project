import { useState } from "react";
import { updatePrivacy } from "../../api/UserApi";

const AccountSettings = ({ jwtToken }) => {
  const [privacy, setPrivacy] = useState("PUBLIC");
  const [message, setMessage] = useState("");

  const handlePrivacyChange = async (e) => {
    const selectedPrivacy = e.target.value;
    setPrivacy(selectedPrivacy);

    try {
      const response = await updatePrivacy(selectedPrivacy, jwtToken);
      setMessage(response);
    } catch (error) {
      setMessage("Failed to update privacy");
    }
  };

  return (
    <div className="flex p-4 border rounded-md max-w-sm">
      <h2 className="text-lg font-medium mb-2">Account Privacy</h2>
      <select
        value={privacy}
        onChange={handlePrivacyChange}
        className="border p-1 rounded"
      >
        <option value="PUBLIC">Public</option>
        <option value="PRIVATE">Private</option>
      </select>
      {message && <p className="mt-2 text-green-600">{message}</p>}
    </div>
  );
};

export default AccountSettings;
